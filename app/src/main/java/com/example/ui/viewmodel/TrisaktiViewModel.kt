package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.SecurityManager
import com.example.data.db.TrisaktiDatabase
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import com.example.data.repository.TrisaktiRepository
import com.example.domain.AuthUser
import com.example.domain.DailyBreakdown
import com.example.domain.FinancialCalculator
import com.example.domain.FinancialSummary
import com.example.domain.TimePeriod
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class TrisaktiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TrisaktiDatabase.getDatabase(application)
    private val repository = TrisaktiRepository(database)
    private val securityManager = SecurityManager(application)

    // Authentication session state (strictly 2 authorized accounts: Sandesh & Father)
    private val _currentUser = MutableStateFlow<AuthUser?>(securityManager.getActiveSession())
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(_currentUser.value != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val allSales: StateFlow<List<SaleEntity>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomers: StateFlow<List<CreditCustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<CreditTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClosings: StateFlow<List<DailyClosingEntity>> = repository.allClosings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected customer for Ledger detail view
    private val _selectedCustomer = MutableStateFlow<CreditCustomerEntity?>(null)
    val selectedCustomer: StateFlow<CreditCustomerEntity?> = _selectedCustomer.asStateFlow()

    // Active report period filter
    private val _reportPeriod = MutableStateFlow(TimePeriod.TODAY)
    val reportPeriod: StateFlow<TimePeriod> = _reportPeriod.asStateFlow()

    // Records date filter
    private val _recordsPeriod = MutableStateFlow(TimePeriod.TODAY)
    val recordsPeriod: StateFlow<TimePeriod> = _recordsPeriod.asStateFlow()

    // Custom date filter for records
    private val _customRecordsDate = MutableStateFlow<String?>(null)
    val customRecordsDate: StateFlow<String?> = _customRecordsDate.asStateFlow()

    // Records type filter (ALL, SALES, EXPENSES)
    private val _recordsTypeFilter = MutableStateFlow("ALL")
    val recordsTypeFilter: StateFlow<String> = _recordsTypeFilter.asStateFlow()

    // Expenses category filter
    private val _selectedExpenseCategory = MutableStateFlow<String?>("All")
    val selectedExpenseCategory: StateFlow<String?> = _selectedExpenseCategory.asStateFlow()

    // PWA Welcome banner state (dismissed once stored in SharedPreferences)
    private val prefs = application.getSharedPreferences("trisakti_app_prefs", android.content.Context.MODE_PRIVATE)
    private val _showWelcomeBanner = MutableStateFlow(!prefs.getBoolean("welcome_dismissed", false))
    val showWelcomeBanner: StateFlow<Boolean> = _showWelcomeBanner.asStateFlow()

    // Notification toast events
    private val _messageEvents = MutableSharedFlow<String>()
    val messageEvents = _messageEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    /**
     * Map of Customer ID to Outstanding balance:
     * Outstanding = sum(CREDIT) - sum(PAYMENT)
     */
    val customerBalances: StateFlow<Map<Long, Double>> = allTransactions.combine(allCustomers) { txs, _ ->
        val balanceMap = mutableMapOf<Long, Double>()
        for (tx in txs) {
            val current = balanceMap.getOrDefault(tx.customerId, 0.0)
            if (tx.type.equals("CREDIT", ignoreCase = true)) {
                balanceMap[tx.customerId] = current + tx.amount
            } else {
                balanceMap[tx.customerId] = (current - tx.amount).coerceAtLeast(0.0)
            }
        }
        balanceMap
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /**
     * Total outstanding credit across all customers.
     */
    val totalOutstandingCredit: StateFlow<Double> = customerBalances.combine(allCustomers) { balances, customers ->
        var sum = 0.0
        for (c in customers) {
            sum += balances.getOrDefault(c.id, 0.0)
        }
        sum
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    /**
     * Today's Business Summary (The Dashboard Core).
     */
    val todaySummary: StateFlow<FinancialSummary> = combine(
        allSales,
        allExpenses,
        allTransactions,
        totalOutstandingCredit
    ) { sales, expenses, txs, totalCreditOutstanding ->
        val todayStr = FinancialCalculator.todayDateString()

        val todaySalesList = sales.filter { it.date == todayStr }
        val todayExpenseList = expenses.filter { it.date == todayStr }
        val todayTxList = txs.filter { it.date == todayStr }

        val totalSales = todaySalesList.sumOf { it.amount }
        val totalCost = todaySalesList.sumOf { it.productCost }
        val totalExpenses = todayExpenseList.sumOf { it.amount }
        val estimatedProfit = FinancialCalculator.calculateEstimatedProfit(totalSales, totalCost, totalExpenses)

        val cashSales = todaySalesList.filter { it.paymentType.equals("Cash", ignoreCase = true) }.sumOf { it.amount }
        val creditSales = todaySalesList.filter { it.paymentType.equals("Credit", ignoreCase = true) }.sumOf { it.amount }
        val otherSales = todaySalesList.filter { !it.paymentType.equals("Cash", ignoreCase = true) && !it.paymentType.equals("Credit", ignoreCase = true) }.sumOf { it.amount }

        val creditGiven = todayTxList.filter { it.type.equals("CREDIT", ignoreCase = true) }.sumOf { it.amount }
        val creditCollected = todayTxList.filter { it.type.equals("PAYMENT", ignoreCase = true) }.sumOf { it.amount }

        FinancialSummary(
            totalSales = totalSales,
            totalProductCost = totalCost,
            totalExpenses = totalExpenses,
            estimatedProfit = estimatedProfit,
            cashSales = cashSales,
            creditSales = creditSales,
            otherSales = otherSales,
            creditGiven = creditGiven,
            creditCollected = creditCollected,
            outstandingCredit = totalCreditOutstanding
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    /**
     * Filtered Summary for Reports screen based on selected TimePeriod.
     */
    val reportSummary: StateFlow<FinancialSummary> = combine(
        allSales,
        allExpenses,
        allTransactions,
        totalOutstandingCredit,
        _reportPeriod
    ) { sales, expenses, txs, totalCreditOutstanding, period ->
        computeSummaryForPeriod(sales, expenses, txs, totalCreditOutstanding, period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    /**
     * Weekly daily breakdown (Sunday through Saturday).
     */
    val weeklyBreakdown: StateFlow<List<DailyBreakdown>> = combine(
        allSales,
        allExpenses
    ) { sales, expenses ->
        val today = try { LocalDate.parse(FinancialCalculator.todayDateString()) } catch (_: Exception) { LocalDate.now() }
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val days = mutableListOf<DailyBreakdown>()
        for (i in 0..6) {
            val date = startOfWeek.plusDays(i.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dayName = date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH))

            val daySales = sales.filter { it.date == dateStr }
            val dayExpenses = expenses.filter { it.date == dateStr }

            val totalSales = daySales.sumOf { it.amount }
            val totalCost = daySales.sumOf { it.productCost }
            val totalExp = dayExpenses.sumOf { it.amount }
            val profit = FinancialCalculator.calculateEstimatedProfit(totalSales, totalCost, totalExp)

            days.add(
                DailyBreakdown(
                    date = dateStr,
                    dayName = dayName,
                    sales = totalSales,
                    productCost = totalCost,
                    expenses = totalExp,
                    profit = profit
                )
            )
        }
        days
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Period summary calculation helper.
     */
    private fun computeSummaryForPeriod(
        sales: List<SaleEntity>,
        expenses: List<ExpenseEntity>,
        txs: List<CreditTransactionEntity>,
        totalCreditOutstanding: Double,
        period: TimePeriod
    ): FinancialSummary {
        val today = try { LocalDate.parse(FinancialCalculator.todayDateString()) } catch (_: Exception) { LocalDate.now() }
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val startDateStr = when (period) {
            TimePeriod.TODAY -> todayStr
            TimePeriod.THIS_WEEK -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).format(DateTimeFormatter.ISO_LOCAL_DATE)
            TimePeriod.THIS_MONTH -> today.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            TimePeriod.CUSTOM -> _customRecordsDate.value ?: todayStr
            TimePeriod.ALL_TIME -> "2000-01-01"
        }

        val filteredSales = if (period == TimePeriod.CUSTOM) sales.filter { it.date == startDateStr } else sales.filter { it.date in startDateStr..todayStr }
        val filteredExpenses = if (period == TimePeriod.CUSTOM) expenses.filter { it.date == startDateStr } else expenses.filter { it.date in startDateStr..todayStr }
        val filteredTxs = if (period == TimePeriod.CUSTOM) txs.filter { it.date == startDateStr } else txs.filter { it.date in startDateStr..todayStr }

        val totalSales = filteredSales.sumOf { it.amount }
        val totalCost = filteredSales.sumOf { it.productCost }
        val totalExpenses = filteredExpenses.sumOf { it.amount }
        val estimatedProfit = FinancialCalculator.calculateEstimatedProfit(totalSales, totalCost, totalExpenses)

        val cashSales = filteredSales.filter { it.paymentType.equals("Cash", ignoreCase = true) }.sumOf { it.amount }
        val creditSales = filteredSales.filter { it.paymentType.equals("Credit", ignoreCase = true) }.sumOf { it.amount }
        val otherSales = filteredSales.filter { !it.paymentType.equals("Cash", ignoreCase = true) && !it.paymentType.equals("Credit", ignoreCase = true) }.sumOf { it.amount }

        val creditGiven = filteredTxs.filter { it.type.equals("CREDIT", ignoreCase = true) }.sumOf { it.amount }
        val creditCollected = filteredTxs.filter { it.type.equals("PAYMENT", ignoreCase = true) }.sumOf { it.amount }

        return FinancialSummary(
            totalSales = totalSales,
            totalProductCost = totalCost,
            totalExpenses = totalExpenses,
            estimatedProfit = estimatedProfit,
            cashSales = cashSales,
            creditSales = creditSales,
            otherSales = otherSales,
            creditGiven = creditGiven,
            creditCollected = creditCollected,
            outstandingCredit = totalCreditOutstanding
        )
    }

    fun selectCustomer(customer: CreditCustomerEntity?) {
        _selectedCustomer.value = customer
    }

    fun setReportPeriod(period: TimePeriod) {
        _reportPeriod.value = period
    }

    fun setRecordsPeriod(period: TimePeriod) {
        _recordsPeriod.value = period
        if (period != TimePeriod.CUSTOM) {
            _customRecordsDate.value = null
        }
    }

    fun setCustomRecordsDate(date: String?) {
        _customRecordsDate.value = date
        if (date != null) {
            _recordsPeriod.value = TimePeriod.CUSTOM
        }
    }

    fun setRecordsTypeFilter(filter: String) {
        _recordsTypeFilter.value = filter
    }

    fun dismissWelcomeBanner() {
        prefs.edit().putBoolean("welcome_dismissed", true).apply()
        _showWelcomeBanner.value = false
    }

    fun resetWelcomeBanner() {
        prefs.edit().putBoolean("welcome_dismissed", false).apply()
        _showWelcomeBanner.value = true
    }

    fun setExpenseCategoryFilter(category: String?) {
        _selectedExpenseCategory.value = category
    }

    fun addSale(
        date: String,
        amount: Double,
        productCost: Double,
        paymentType: String,
        description: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertSale(
                SaleEntity(
                    date = date,
                    amount = amount,
                    productCost = productCost,
                    paymentType = paymentType,
                    description = description,
                    notes = notes
                )
            )
            _messageEvents.emit("Sale of ${FinancialCalculator.formatNpr(amount)} recorded successfully!")
        }
    }

    fun updateSale(sale: SaleEntity) {
        viewModelScope.launch {
            repository.updateSale(sale)
            _messageEvents.emit("Sale of ${FinancialCalculator.formatNpr(sale.amount)} updated successfully!")
        }
    }

    fun deleteSale(sale: SaleEntity) {
        viewModelScope.launch {
            repository.deleteSale(sale)
            _messageEvents.emit("Sale record deleted.")
        }
    }

    fun addExpense(
        date: String,
        amount: Double,
        category: String,
        description: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    date = date,
                    amount = amount,
                    category = category,
                    description = description,
                    notes = notes
                )
            )
            _messageEvents.emit("Expense of ${FinancialCalculator.formatNpr(amount)} added under $category.")
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            _messageEvents.emit("Expense of ${FinancialCalculator.formatNpr(expense.amount)} updated successfully!")
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _messageEvents.emit("Expense record deleted.")
        }
    }

    fun addCustomer(name: String, phone: String, notes: String, initialCredit: Double = 0.0) {
        viewModelScope.launch {
            val customerId = repository.insertCustomer(
                CreditCustomerEntity(
                    name = name.trim(),
                    phone = phone.trim(),
                    notes = notes.trim()
                )
            )
            if (initialCredit > 0.0) {
                repository.insertTransaction(
                    CreditTransactionEntity(
                        customerId = customerId,
                        date = FinancialCalculator.todayDateString(),
                        type = "CREDIT",
                        amount = initialCredit,
                        description = "Initial credit balance"
                    )
                )
            }
            _messageEvents.emit("Customer $name added to credit ledger!")
        }
    }

    fun deleteCustomer(customer: CreditCustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_selectedCustomer.value?.id == customer.id) {
                _selectedCustomer.value = null
            }
            _messageEvents.emit("Customer ${customer.name} removed from ledger.")
        }
    }

    fun addCredit(customerId: Long, date: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                CreditTransactionEntity(
                    customerId = customerId,
                    date = date,
                    type = "CREDIT",
                    amount = amount,
                    description = description
                )
            )
            _messageEvents.emit("Credit of ${FinancialCalculator.formatNpr(amount)} added to account.")
        }
    }

    fun recordPayment(customerId: Long, date: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                CreditTransactionEntity(
                    customerId = customerId,
                    date = date,
                    type = "PAYMENT",
                    amount = amount,
                    description = description
                )
            )
            _messageEvents.emit("Payment of ${FinancialCalculator.formatNpr(amount)} recorded!")
        }
    }

    fun deleteCreditTransaction(tx: CreditTransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            _messageEvents.emit("Ledger entry deleted.")
        }
    }

    fun saveDailyClosing(
        date: String,
        openingCash: Double,
        actualCash: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val todaySummaryVal = todaySummary.value
            val expectedCash = FinancialCalculator.calculateExpectedCash(
                openingCash = openingCash,
                cashSales = todaySummaryVal.cashSales,
                cashExpenses = todaySummaryVal.totalExpenses,
                creditPayments = todaySummaryVal.creditCollected
            )
            val difference = actualCash - expectedCash

            repository.insertClosing(
                DailyClosingEntity(
                    date = date,
                    openingCash = openingCash,
                    cashSales = todaySummaryVal.cashSales,
                    cashExpenses = todaySummaryVal.totalExpenses,
                    creditSales = todaySummaryVal.creditSales,
                    creditPayments = todaySummaryVal.creditCollected,
                    expectedCash = expectedCash,
                    actualCash = actualCash,
                    difference = difference,
                    notes = notes
                )
            )
            val diffFormatted = FinancialCalculator.formatNpr(Math.abs(difference))
            val status = if (difference == 0.0) "Balanced perfectly!" else if (difference > 0) "+$diffFormatted Surplus" else "-$diffFormatted Shortfall"
            _messageEvents.emit("Daily closing saved for $date ($status)")
        }
    }

    fun deleteClosing(closing: DailyClosingEntity) {
        viewModelScope.launch {
            repository.deleteClosing(closing)
            _messageEvents.emit("Closing record removed.")
        }
    }

    fun reloadDemoData() {
        viewModelScope.launch {
            repository.resetAndLoadDemoData()
            _messageEvents.emit("Demo data successfully reloaded!")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedCustomer.value = null
            _messageEvents.emit("All records cleared. Database is empty.")
        }
    }

    /**
     * Authenticates an authorized family business user.
     */
    fun login(emailOrId: String, passkey: String): Result<AuthUser> {
        val result = securityManager.authenticate(emailOrId, passkey)
        if (result.isSuccess) {
            val user = result.getOrNull()
            _currentUser.value = user
            _isLoggedIn.value = true
            viewModelScope.launch {
                _messageEvents.emit("Welcome back, ${user?.name}!")
            }
        }
        return result
    }

    /**
     * Securely changes the user's password and updates the session state.
     */
    fun changePassword(currentPasskey: String, newPasskey: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("No active session."))
        val result = securityManager.changePassword(user.id, currentPasskey, newPasskey)
        if (result.isSuccess) {
            _currentUser.value = user.copy(requiresPasswordChange = false)
            viewModelScope.launch {
                _messageEvents.emit("Password successfully changed.")
            }
        }
        return result
    }

    /**
     * Terminates the active business session and wipes memory state.
     */
    fun logout() {
        securityManager.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
        _selectedCustomer.value = null
        viewModelScope.launch {
            _messageEvents.emit("Logged out securely. Session ended.")
        }
    }
}

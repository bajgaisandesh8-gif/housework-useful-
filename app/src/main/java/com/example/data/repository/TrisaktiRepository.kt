package com.example.data.repository

import com.example.data.db.TrisaktiDatabase
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import com.example.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.from

class TrisaktiRepository(@Suppress("UNUSED_PARAMETER") database: TrisaktiDatabase) {
    private val supabase = SupabaseClientProvider.client

    private val _sales = MutableStateFlow<List<SaleEntity>>(emptyList())
    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    private val _customers = MutableStateFlow<List<CreditCustomerEntity>>(emptyList())
    private val _transactions = MutableStateFlow<List<CreditTransactionEntity>>(emptyList())
    private val _closings = MutableStateFlow<List<DailyClosingEntity>>(emptyList())

    val allSales: Flow<List<SaleEntity>> = _sales
    val allExpenses: Flow<List<ExpenseEntity>> = _expenses
    val allCustomers: Flow<List<CreditCustomerEntity>> = _customers
    val allTransactions: Flow<List<CreditTransactionEntity>> = _transactions
    val allClosings: Flow<List<DailyClosingEntity>> = _closings

    @Serializable
    private data class SaleRow(
        val id: Long? = null,
        val date: String,
        val amount: Double,
        @SerialName("product_cost") val productCost: Double,
        @SerialName("payment_type") val paymentType: String,
        val description: String = "",
        val notes: String? = "",
        @SerialName("recorded_by") val recordedBy: String? = null
    )

    @Serializable
    private data class ExpenseRow(
        val id: Long? = null,
        val date: String,
        val amount: Double,
        val category: String,
        val description: String = "",
        val notes: String? = "",
        @SerialName("recorded_by") val recordedBy: String? = null
    )

    @Serializable
    private data class CustomerRow(
        val id: Long? = null,
        val name: String,
        val phone: String? = "",
        val notes: String? = "",
        @SerialName("initial_credit") val initialCredit: Double? = 0.0
    )

    @Serializable
    private data class TransactionRow(
        val id: Long? = null,
        @SerialName("customer_id") val customerId: Long,
        val date: String,
        val type: String,
        val amount: Double,
        val description: String? = "",
        @SerialName("recorded_by") val recordedBy: String? = null
    )

    @Serializable
    private data class ClosingRow(
        val id: Long? = null,
        val date: String,
        @SerialName("opening_cash") val openingCash: Double,
        @SerialName("cash_sales") val cashSales: Double,
        @SerialName("cash_expenses") val cashExpenses: Double,
        @SerialName("credit_sales") val creditSales: Double,
        @SerialName("credit_payments") val creditPayments: Double,
        @SerialName("expected_cash") val expectedCash: Double,
        @SerialName("actual_cash") val actualCash: Double,
        val difference: Double,
        val notes: String? = "",
        @SerialName("recorded_by") val recordedBy: String? = null
    )

    private suspend fun recordedBy(): String =
        supabase.auth.currentSessionOrNull()?.user?.id
            ?: throw IllegalStateException("Your session has expired. Please sign in again.")

    suspend fun refresh() = withContext(Dispatchers.IO) {
        _sales.value = supabase.from("sales").select().decodeList<SaleRow>().map { it.toEntity() }
        _expenses.value = supabase.from("expenses").select().decodeList<ExpenseRow>().map { it.toEntity() }
        _customers.value = supabase.from("credit_customers").select().decodeList<CustomerRow>().map { it.toEntity() }
        _transactions.value = supabase.from("credit_transactions").select().decodeList<TransactionRow>().map { it.toEntity() }
        _closings.value = supabase.from("daily_closings").select().decodeList<ClosingRow>().map { it.toEntity() }
    }

    fun getTransactionsForCustomer(customerId: Long): Flow<List<CreditTransactionEntity>> =
        kotlinx.coroutines.flow.flow {
            emit(_transactions.value.filter { it.customerId == customerId })
        }

    suspend fun insertSale(sale: SaleEntity): Long = withContext(Dispatchers.IO) {
        val row = SaleRow(
            date = sale.date,
            amount = sale.amount,
            productCost = sale.productCost,
            paymentType = sale.paymentType.uppercase(),
            description = sale.description.trim(),
            notes = sale.notes.trim(),
            recordedBy = recordedBy()
        )
        val inserted = supabase.from("sales").insert(row) { select() }.decodeSingle<SaleRow>()
        refresh()
        inserted.id ?: error("Sale was saved but no record ID was returned.")
    }

    suspend fun updateSale(sale: SaleEntity) = withContext(Dispatchers.IO) {
        supabase.from("sales").update(
            SaleRow(date = sale.date, amount = sale.amount, productCost = sale.productCost,
                paymentType = sale.paymentType.uppercase(), description = sale.description.trim(),
                notes = sale.notes.trim(), recordedBy = recordedBy())
        ) { filter { eq("id", sale.id) } }
        refresh()
    }

    suspend fun deleteSale(sale: SaleEntity) = withContext(Dispatchers.IO) {
        supabase.from("sales").delete { filter { eq("id", sale.id) } }
        refresh()
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long = withContext(Dispatchers.IO) {
        val inserted = supabase.from("expenses").insert(
            ExpenseRow(date = expense.date, amount = expense.amount, category = expense.category.trim(),
                description = expense.description.trim(), notes = expense.notes.trim(), recordedBy = recordedBy())
        ) { select() }.decodeSingle<ExpenseRow>()
        refresh()
        inserted.id ?: error("Expense was saved but no record ID was returned.")
    }

    suspend fun updateExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        supabase.from("expenses").update(
            ExpenseRow(date = expense.date, amount = expense.amount, category = expense.category.trim(),
                description = expense.description.trim(), notes = expense.notes.trim(), recordedBy = recordedBy())
        ) { filter { eq("id", expense.id) } }
        refresh()
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        supabase.from("expenses").delete { filter { eq("id", expense.id) } }
        refresh()
    }

    suspend fun insertCustomer(customer: CreditCustomerEntity): Long = withContext(Dispatchers.IO) {
        val inserted = supabase.from("credit_customers").insert(
            CustomerRow(name = customer.name.trim(), phone = customer.phone.trim(), notes = customer.notes.trim())
        ) { select() }.decodeSingle<CustomerRow>()
        refresh()
        inserted.id ?: error("Customer was saved but no record ID was returned.")
    }

    suspend fun updateCustomer(customer: CreditCustomerEntity) = withContext(Dispatchers.IO) {
        supabase.from("credit_customers").update(
            CustomerRow(name = customer.name.trim(), phone = customer.phone.trim(), notes = customer.notes.trim())
        ) { filter { eq("id", customer.id) } }
        refresh()
    }

    suspend fun deleteCustomer(customer: CreditCustomerEntity) = withContext(Dispatchers.IO) {
        supabase.from("credit_customers").delete { filter { eq("id", customer.id) } }
        refresh()
    }

    suspend fun insertTransaction(transaction: CreditTransactionEntity): Long = withContext(Dispatchers.IO) {
        val inserted = supabase.from("credit_transactions").insert(
            TransactionRow(customerId = transaction.customerId, date = transaction.date,
                type = transaction.type.uppercase(), amount = transaction.amount,
                description = transaction.description.trim(), recordedBy = recordedBy())
        ) { select() }.decodeSingle<TransactionRow>()
        refresh()
        inserted.id ?: error("Ledger entry was saved but no record ID was returned.")
    }

    suspend fun deleteTransaction(transaction: CreditTransactionEntity) = withContext(Dispatchers.IO) {
        supabase.from("credit_transactions").delete { filter { eq("id", transaction.id) } }
        refresh()
    }

    suspend fun insertClosing(closing: DailyClosingEntity): Long = withContext(Dispatchers.IO) {
        val inserted = supabase.from("daily_closings").insert(
            ClosingRow(date = closing.date, openingCash = closing.openingCash, cashSales = closing.cashSales,
                cashExpenses = closing.cashExpenses, creditSales = closing.creditSales,
                creditPayments = closing.creditPayments, expectedCash = closing.expectedCash,
                actualCash = closing.actualCash, difference = closing.difference,
                notes = closing.notes.trim(), recordedBy = recordedBy())
        ) { select() }.decodeSingle<ClosingRow>()
        refresh()
        inserted.id ?: error("Daily closing was saved but no record ID was returned.")
    }

    suspend fun deleteClosing(closing: DailyClosingEntity) = withContext(Dispatchers.IO) {
        supabase.from("daily_closings").delete { filter { eq("id", closing.id) } }
        refresh()
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        if (supabase.auth.currentSessionOrNull() != null) refresh()
    }

    suspend fun resetAndLoadDemoData() {
        throw UnsupportedOperationException("Demo data is disabled for the production shared business ledger.")
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        // Intentionally disabled: production shared data must not have a bulk-delete path.
        throw UnsupportedOperationException("Bulk delete is disabled for safety.")
    }

    private fun SaleRow.toEntity() = SaleEntity(id ?: 0, date, amount, productCost, paymentType, description, notes ?: "")
    private fun ExpenseRow.toEntity() = ExpenseEntity(id ?: 0, date, amount, category, description, notes ?: "")
    private fun CustomerRow.toEntity() = CreditCustomerEntity(id ?: 0, name, phone ?: "", notes ?: "")
    private fun TransactionRow.toEntity() = CreditTransactionEntity(id ?: 0, customerId, date, type, amount, description ?: "")
    private fun ClosingRow.toEntity() = DailyClosingEntity(id ?: 0, date, openingCash, cashSales, cashExpenses, creditSales, creditPayments, expectedCash, actualCash, difference, notes ?: "")
}

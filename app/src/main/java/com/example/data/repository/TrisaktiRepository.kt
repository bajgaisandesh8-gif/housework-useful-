package com.example.data.repository

import com.example.data.dao.CreditDao
import com.example.data.dao.DailyClosingDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.SaleDao
import com.example.data.db.DemoData
import com.example.data.db.TrisaktiDatabase
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class TrisaktiRepository(
    private val database: TrisaktiDatabase,
    private val saleDao: SaleDao = database.saleDao(),
    private val expenseDao: ExpenseDao = database.expenseDao(),
    private val creditDao: CreditDao = database.creditDao(),
    private val dailyClosingDao: DailyClosingDao = database.dailyClosingDao()
) {
    val allSales: Flow<List<SaleEntity>> = saleDao.getAllSales()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCustomers: Flow<List<CreditCustomerEntity>> = creditDao.getAllCustomers()
    val allTransactions: Flow<List<CreditTransactionEntity>> = creditDao.getAllTransactions()
    val allClosings: Flow<List<DailyClosingEntity>> = dailyClosingDao.getAllClosings()

    fun getTransactionsForCustomer(customerId: Long): Flow<List<CreditTransactionEntity>> {
        return creditDao.getTransactionsForCustomer(customerId)
    }

    suspend fun insertSale(sale: SaleEntity): Long = withContext(Dispatchers.IO) {
        saleDao.insertSale(sale)
    }

    suspend fun updateSale(sale: SaleEntity) = withContext(Dispatchers.IO) {
        saleDao.updateSale(sale)
    }

    suspend fun deleteSale(sale: SaleEntity) = withContext(Dispatchers.IO) {
        saleDao.deleteSale(sale)
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun insertCustomer(customer: CreditCustomerEntity): Long = withContext(Dispatchers.IO) {
        creditDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CreditCustomerEntity) = withContext(Dispatchers.IO) {
        creditDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CreditCustomerEntity) = withContext(Dispatchers.IO) {
        creditDao.deleteCustomer(customer)
    }

    suspend fun insertTransaction(transaction: CreditTransactionEntity): Long = withContext(Dispatchers.IO) {
        creditDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: CreditTransactionEntity) = withContext(Dispatchers.IO) {
        creditDao.deleteTransaction(transaction)
    }

    suspend fun insertClosing(closing: DailyClosingEntity): Long = withContext(Dispatchers.IO) {
        dailyClosingDao.insertClosing(closing)
    }

    suspend fun deleteClosing(closing: DailyClosingEntity) = withContext(Dispatchers.IO) {
        dailyClosingDao.deleteClosing(closing)
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existingSales = allSales.firstOrNull() ?: emptyList()
        val existingExpenses = allExpenses.firstOrNull() ?: emptyList()
        if (existingSales.isEmpty() && existingExpenses.isEmpty()) {
            TrisaktiDatabase.populateInitialDemoData(database)
        }
    }

    suspend fun resetAndLoadDemoData() = withContext(Dispatchers.IO) {
        clearAllData()
        TrisaktiDatabase.populateInitialDemoData(database)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        saleDao.clearAll()
        expenseDao.clearAll()
        creditDao.clearAllTransactions()
        creditDao.clearAllCustomers()
        dailyClosingDao.clearAll()
    }
}

package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC, id DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE date = :date ORDER BY id DESC")
    fun getSalesByDate(date: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, id DESC")
    fun getSalesInRange(startDate: String, endDate: String): Flow<List<SaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long

    @Update
    suspend fun updateSale(sale: SaleEntity)

    @Delete
    suspend fun deleteSale(sale: SaleEntity)

    @Query("DELETE FROM sales")
    suspend fun clearAll()
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id DESC")
    fun getExpensesByDate(date: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, id DESC")
    fun getExpensesInRange(startDate: String, endDate: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun clearAll()
}

@Dao
interface CreditDao {
    @Query("SELECT * FROM credit_customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CreditCustomerEntity>>

    @Query("SELECT * FROM credit_customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): CreditCustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CreditCustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CreditCustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CreditCustomerEntity)

    @Query("SELECT * FROM credit_transactions WHERE customer_id = :customerId ORDER BY date DESC, id DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<CreditTransactionEntity>>

    @Query("SELECT * FROM credit_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<CreditTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CreditTransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: CreditTransactionEntity)

    @Query("DELETE FROM credit_transactions")
    suspend fun clearAllTransactions()

    @Query("DELETE FROM credit_customers")
    suspend fun clearAllCustomers()
}

@Dao
interface DailyClosingDao {
    @Query("SELECT * FROM daily_closing ORDER BY date DESC, id DESC")
    fun getAllClosings(): Flow<List<DailyClosingEntity>>

    @Query("SELECT * FROM daily_closing WHERE date = :date ORDER BY id DESC LIMIT 1")
    suspend fun getClosingByDate(date: String): DailyClosingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClosing(closing: DailyClosingEntity): Long

    @Delete
    suspend fun deleteClosing(closing: DailyClosingEntity)

    @Query("DELETE FROM daily_closing")
    suspend fun clearAll()
}

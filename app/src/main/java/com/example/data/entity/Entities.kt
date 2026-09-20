package com.example.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily sales records.
 * Equivalent to Supabase table: sales
 */
@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // ISO date YYYY-MM-DD
    val amount: Double,
    @ColumnInfo(name = "product_cost")
    val productCost: Double,
    @ColumnInfo(name = "payment_type")
    val paymentType: String, // "Cash", "Credit", "Other"
    val description: String,
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Operating expenses records.
 * Equivalent to Supabase table: expenses
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // ISO date YYYY-MM-DD
    val amount: Double,
    val category: String, // "Rent", "Electricity", "Transportation", etc.
    val description: String,
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Credit / Udhaar customer ledger profiles.
 * Equivalent to Supabase table: credit_customers
 */
@Entity(tableName = "credit_customers")
data class CreditCustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Credit transactions (either new credit given or repayment collected).
 * Equivalent to Supabase table: credit_transactions
 */
@Entity(
    tableName = "credit_transactions",
    foreignKeys = [
        ForeignKey(
            entity = CreditCustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customer_id"])]
)
data class CreditTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "customer_id")
    val customerId: Long,
    val date: String, // ISO date YYYY-MM-DD
    val type: String, // "CREDIT" or "PAYMENT"
    val amount: Double,
    val description: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Daily drawer closing records for tracking physical cash vs recorded transactions.
 * Equivalent to Supabase table: daily_closing
 */
@Entity(tableName = "daily_closing")
data class DailyClosingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // ISO date YYYY-MM-DD
    @ColumnInfo(name = "opening_cash")
    val openingCash: Double,
    @ColumnInfo(name = "cash_sales")
    val cashSales: Double,
    @ColumnInfo(name = "cash_expenses")
    val cashExpenses: Double,
    @ColumnInfo(name = "credit_sales")
    val creditSales: Double,
    @ColumnInfo(name = "credit_payments")
    val creditPayments: Double,
    @ColumnInfo(name = "expected_cash")
    val expectedCash: Double,
    @ColumnInfo(name = "actual_cash")
    val actualCash: Double,
    val difference: Double,
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

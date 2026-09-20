package com.example.domain

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Supported payment types for sales.
 */
enum class PaymentType(val displayName: String) {
    CASH("Cash"),
    CREDIT("Credit"),
    OTHER("Other")
}

/**
 * Standard expense categories for retail trading.
 */
enum class ExpenseCategory(val displayName: String) {
    RENT("Rent"),
    ELECTRICITY("Electricity"),
    TRANSPORTATION("Transportation"),
    STAFF("Staff"),
    MAINTENANCE("Maintenance"),
    SUPPLIES("Supplies"),
    OTHER("Other")
}

/**
 * Type of credit transaction.
 */
enum class CreditType(val displayName: String) {
    CREDIT("Credit Given"),
    PAYMENT("Payment Received")
}

/**
 * Period filter for financial summaries and reports.
 */
enum class TimePeriod(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    CUSTOM("Custom Date"),
    ALL_TIME("All Time")
}

/**
 * Aggregated financial summary for any selected time window.
 */
data class FinancialSummary(
    val totalSales: Double = 0.0,
    val totalProductCost: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val estimatedProfit: Double = 0.0, // Total Sales - COGS - Operating Expenses
    val cashSales: Double = 0.0,
    val creditSales: Double = 0.0,
    val otherSales: Double = 0.0,
    val creditGiven: Double = 0.0,
    val creditCollected: Double = 0.0,
    val outstandingCredit: Double = 0.0
)

/**
 * Daily breakdown for weekly and monthly reports.
 */
data class DailyBreakdown(
    val date: String,
    val dayName: String,
    val sales: Double,
    val productCost: Double,
    val expenses: Double,
    val profit: Double
)

/**
 * Centralized financial calculations and Nepali Rupee formatting.
 */
object FinancialCalculator {

    /**
     * Standard formula:
     * Estimated Profit = Total Sales - Cost of Goods Sold (Product Cost) - Operating Expenses
     */
    fun calculateEstimatedProfit(
        sales: Double,
        productCost: Double,
        operatingExpenses: Double
    ): Double {
        return sales - productCost - operatingExpenses
    }

    /**
     * Customer Outstanding calculation:
     * Outstanding = Total Credit Given - Total Payments Received
     */
    fun calculateOutstandingBalance(
        totalCredit: Double,
        totalPayments: Double
    ): Double {
        return (totalCredit - totalPayments).coerceAtLeast(0.0)
    }

    /**
     * Expected Cash in Drawer at closing:
     * Expected Cash = Opening Cash + Cash Sales - Cash Expenses + Credit Payments Collected
     */
    fun calculateExpectedCash(
        openingCash: Double,
        cashSales: Double,
        cashExpenses: Double,
        creditPayments: Double
    ): Double {
        return openingCash + cashSales - cashExpenses + creditPayments
    }

    /**
     * Format numbers into clean Nepali Rupees representation: e.g. "Rs. 25,000"
     */
    fun formatNpr(amount: Double): String {
        val rounded = Math.round(amount)
        val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
        return "Rs. " + format.format(rounded)
    }

    /**
     * Format numbers without currency prefix: e.g. "25,000"
     */
    fun formatNumberOnly(amount: Double): String {
        val rounded = Math.round(amount)
        val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
        return format.format(rounded)
    }

    /**
     * Calculate profit margin percentage (e.g. 16%)
     */
    fun calculateProfitMargin(sales: Double, profit: Double): Int {
        if (sales <= 0.0) return 0
        return ((profit / sales) * 100).toInt()
    }

    /**
     * Calculate percentage difference for month-over-month comparison:
     * Returns null if prior value is 0 or insufficient data.
     */
    fun calculatePercentageChange(current: Double, prior: Double): Double? {
        if (prior <= 0.0) return null
        return ((current - prior) / prior) * 100.0
    }

    /**
     * Helper to get today's ISO date string (YYYY-MM-DD).
     */
    fun todayDateString(): String {
        return try {
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (_: Exception) {
            "2026-09-20"
        }
    }

    /**
     * Helper to format YYYY-MM-DD to friendly human readable date, e.g. "Sep 20, 2026".
     */
    fun formatDisplayDate(isoDate: String): String {
        return try {
            val parsed = LocalDate.parse(isoDate)
            parsed.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
        } catch (_: Exception) {
            isoDate
        }
    }
}

package com.example.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculatorTest {

    @Test
    fun calculateEstimatedProfit_exactMatchFromPrompt() {
        // Example from prompt:
        // Sales = Rs. 25,000, Product Cost = Rs. 17,000, Expenses = Rs. 4,000
        // Estimated Profit = 25,000 - 17,000 - 4,000 = Rs. 4,000
        val sales = 25000.0
        val cost = 17000.0
        val expenses = 4000.0

        val profit = FinancialCalculator.calculateEstimatedProfit(sales, cost, expenses)
        assertEquals(4000.0, profit, 0.001)
    }

    @Test
    fun calculateExpectedCash_exactMatchFromPrompt() {
        // Example from prompt:
        // Opening Cash = Rs. 10,000
        // Cash Sales = Rs. 18,500
        // Cash Expenses = Rs. 3,200
        // Credit Payments = Rs. 1,500
        // Expected Cash = 10,000 + 18,500 - 3,200 + 1,500 = Rs. 26,800
        val opening = 10000.0
        val cashSales = 18500.0
        val cashExpenses = 3200.0
        val creditPayments = 1500.0

        val expected = FinancialCalculator.calculateExpectedCash(opening, cashSales, cashExpenses, creditPayments)
        assertEquals(26800.0, expected, 0.001)

        val actualCash = 26500.0
        val difference = actualCash - expected
        assertEquals(-300.0, difference, 0.001)
    }

    @Test
    fun calculateOutstandingBalance_exactMatchFromPrompt() {
        // Customer: Ram
        // Sep 18: Credit +1500
        // Sep 19: Credit +2000
        // Sep 20: Payment -1000
        // Net = 1500 + 2000 - 1000 = 2500
        val totalCredit = 3500.0
        val totalPayments = 1000.0

        val outstanding = FinancialCalculator.calculateOutstandingBalance(totalCredit, totalPayments)
        assertEquals(2500.0, outstanding, 0.001)
    }

    @Test
    fun formatNpr_formatsCorrectlyWithPrefix() {
        val formatted = FinancialCalculator.formatNpr(25000.0)
        assertTrue(formatted.startsWith("Rs. "))
        assertTrue(formatted.contains("25,000") || formatted.contains("25000"))
    }
}

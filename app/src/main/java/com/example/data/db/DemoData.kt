package com.example.data.db

import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity

/**
 * Realistic Nepali Retail demo data for Trisakti Traders.
 * Clearly identified as demo records, allowing the parents to experience
 * the entire application immediately and easily purge or reset at any time.
 */
object DemoData {

    fun getDemoSales(): List<SaleEntity> {
        return listOf(
            SaleEntity(
                date = "2026-09-20",
                amount = 25000.0,
                productCost = 17000.0,
                paymentType = "Cash",
                description = "25kg Mansuli Rice (10 bags) & Sunflower Oil (4 cartons)",
                notes = "Wholesale morning customer delivery"
            ),
            SaleEntity(
                date = "2026-09-20",
                amount = 4000.0,
                productCost = 2800.0,
                paymentType = "Credit",
                description = "Spices batch (Jeera, Besan, Haldi) & Packaged Lentils",
                notes = "Recorded under Ram Sharma udhaar ledger"
            ),
            SaleEntity(
                date = "2026-09-19",
                amount = 18500.0,
                productCost = 12500.0,
                paymentType = "Cash",
                description = "Dairy products, Chiura, Tea packets, & Sugar 50kg",
                notes = "Daily retail walk-in sales"
            ),
            SaleEntity(
                date = "2026-09-19",
                amount = 6200.0,
                productCost = 4300.0,
                paymentType = "Other",
                description = "Biscuits & Confectionery Cartons",
                notes = "Paid via eSewa QR"
            ),
            SaleEntity(
                date = "2026-09-18",
                amount = 22400.0,
                productCost = 15200.0,
                paymentType = "Cash",
                description = "Mustard Oil drums & Flour 30kg sacks",
                notes = "Bafal restaurant bulk purchase"
            ),
            SaleEntity(
                date = "2026-09-17",
                amount = 16800.0,
                productCost = 11400.0,
                paymentType = "Cash",
                description = "Soap, Detergents & Cleaning goods",
                notes = "Retail counter sales"
            ),
            SaleEntity(
                date = "2026-09-16",
                amount = 19200.0,
                productCost = 13000.0,
                paymentType = "Cash",
                description = "Dry fruits, Chana & Rajma sacks",
                notes = "Evening rush hour"
            ),
            SaleEntity(
                date = "2026-09-15",
                amount = 14500.0,
                productCost = 9800.0,
                paymentType = "Cash",
                description = "Spices, Noodles cartons & Cooking Oil",
                notes = "Regular counter sales"
            ),
            SaleEntity(
                date = "2026-09-14",
                amount = 21000.0,
                productCost = 14200.0,
                paymentType = "Cash",
                description = "Basmati Rice & Ghee containers",
                notes = "Festival prep stock order"
            )
        )
    }

    fun getDemoExpenses(): List<ExpenseEntity> {
        return listOf(
            ExpenseEntity(
                date = "2026-09-20",
                amount = 4000.0,
                category = "Transportation",
                description = "Mini-truck delivery freight from Kalimati wholesale depot",
                notes = "Paid to driver Shyam"
            ),
            ExpenseEntity(
                date = "2026-09-19",
                amount = 3200.0,
                category = "Supplies",
                description = "Store packing bags, plastic rolls & receipt paper",
                notes = "Purchased from Ason wholesale"
            ),
            ExpenseEntity(
                date = "2026-09-18",
                amount = 8500.0,
                category = "Electricity",
                description = "Nepal Electricity Authority (NEA) commercial store bill",
                notes = "Bhadra month electricity payment"
            ),
            ExpenseEntity(
                date = "2026-09-15",
                amount = 12000.0,
                category = "Staff",
                description = "Bi-weekly assistant wages (Ramesh & Sunil)",
                notes = "Shop helpers allowance"
            ),
            ExpenseEntity(
                date = "2026-09-01",
                amount = 20000.0,
                category = "Rent",
                description = "Monthly store shutter rent (Kalanki main road)",
                notes = "Paid via bank transfer to landlord"
            ),
            ExpenseEntity(
                date = "2026-09-10",
                amount = 2500.0,
                category = "Maintenance",
                description = "Digital weighing scale calibration & shutter lock repair",
                notes = "Local technician"
            )
        )
    }

    fun getDemoCustomers(): List<CreditCustomerEntity> {
        return listOf(
            CreditCustomerEntity(
                id = 1,
                name = "Ram Sharma",
                phone = "9841234567",
                notes = "Local hotel owner in Kalanki, pays bi-weekly"
            ),
            CreditCustomerEntity(
                id = 2,
                name = "Shyam Shrestha",
                phone = "9851098765",
                notes = "Neighboring tea shop, weekly settlement"
            ),
            CreditCustomerEntity(
                id = 3,
                name = "Hari Thapa",
                phone = "9813456789",
                notes = "Catering supplier, reliable payer on 1st of month"
            ),
            CreditCustomerEntity(
                id = 4,
                name = "Sita Devi Adhikari",
                phone = "9860112233",
                notes = "Residential household grocery monthly account"
            ),
            CreditCustomerEntity(
                id = 5,
                name = "Krishna Prasad Bhattarai",
                phone = "9801234567",
                notes = "Snack corner shop owner"
            )
        )
    }

    fun getDemoTransactions(): List<CreditTransactionEntity> {
        return listOf(
            // Ram Sharma: Sep 18 (+1500), Sep 19 (+2000), Sep 20 (-1000) -> Net 2500, plus older 2000 = 4500
            CreditTransactionEntity(
                customerId = 1,
                date = "2026-09-15",
                type = "CREDIT",
                amount = 2000.0,
                description = "Rice & Spices order"
            ),
            CreditTransactionEntity(
                customerId = 1,
                date = "2026-09-18",
                type = "CREDIT",
                amount = 1500.0,
                description = "Sunflower cooking oil"
            ),
            CreditTransactionEntity(
                customerId = 1,
                date = "2026-09-19",
                type = "CREDIT",
                amount = 2000.0,
                description = "Sugar & Flour sacks"
            ),
            CreditTransactionEntity(
                customerId = 1,
                date = "2026-09-20",
                type = "PAYMENT",
                amount = 1000.0,
                description = "Cash partial payment received"
            ),

            // Shyam Shrestha: Net 2000
            CreditTransactionEntity(
                customerId = 2,
                date = "2026-09-16",
                type = "CREDIT",
                amount = 3500.0,
                description = "Tea leaves & Milk powder boxes"
            ),
            CreditTransactionEntity(
                customerId = 2,
                date = "2026-09-19",
                type = "PAYMENT",
                amount = 1500.0,
                description = "Cash repayment at shop counter"
            ),

            // Hari Thapa: Net 7200
            CreditTransactionEntity(
                customerId = 3,
                date = "2026-09-14",
                type = "CREDIT",
                amount = 9200.0,
                description = "Bulk party catering food ingredients"
            ),
            CreditTransactionEntity(
                customerId = 3,
                date = "2026-09-17",
                type = "PAYMENT",
                amount = 2000.0,
                description = "Advance partial deposit"
            ),

            // Sita Devi: Net 3100
            CreditTransactionEntity(
                customerId = 4,
                date = "2026-09-12",
                type = "CREDIT",
                amount = 4600.0,
                description = "Household monthly dry provisions"
            ),
            CreditTransactionEntity(
                customerId = 4,
                date = "2026-09-18",
                type = "PAYMENT",
                amount = 1500.0,
                description = "Payment via mobile banking"
            ),

            // Krishna Prasad: Net 1800
            CreditTransactionEntity(
                customerId = 5,
                date = "2026-09-13",
                type = "CREDIT",
                amount = 1800.0,
                description = "Noodles cartons & cooking oil"
            )
        )
    }

    fun getDemoClosings(): List<DailyClosingEntity> {
        return listOf(
            DailyClosingEntity(
                date = "2026-09-19",
                openingCash = 10000.0,
                cashSales = 18500.0,
                cashExpenses = 3200.0,
                creditSales = 0.0,
                creditPayments = 1500.0,
                expectedCash = 26800.0,
                actualCash = 26500.0,
                difference = -300.0,
                notes = "Rs. 300 discrepancy verified as small coin change given to customer"
            )
        )
    }
}

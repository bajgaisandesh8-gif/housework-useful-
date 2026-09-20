package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CreditDao
import com.example.data.dao.DailyClosingDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.SaleDao
import com.example.data.entity.CreditCustomerEntity
import com.example.data.entity.CreditTransactionEntity
import com.example.data.entity.DailyClosingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.SaleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SaleEntity::class,
        ExpenseEntity::class,
        CreditCustomerEntity::class,
        CreditTransactionEntity::class,
        DailyClosingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TrisaktiDatabase : RoomDatabase() {

    abstract fun saleDao(): SaleDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun creditDao(): CreditDao
    abstract fun dailyClosingDao(): DailyClosingDao

    companion object {
        @Volatile
        private var INSTANCE: TrisaktiDatabase? = null

        fun getDatabase(context: Context): TrisaktiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrisaktiDatabase::class.java,
                    "trisakti_traders.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate demo data asynchronously in background
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialDemoData(database)
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialDemoData(db: TrisaktiDatabase) {
            val saleDao = db.saleDao()
            val expenseDao = db.expenseDao()
            val creditDao = db.creditDao()
            val closingDao = db.dailyClosingDao()

            for (sale in DemoData.getDemoSales()) {
                saleDao.insertSale(sale)
            }
            for (expense in DemoData.getDemoExpenses()) {
                expenseDao.insertExpense(expense)
            }
            for (customer in DemoData.getDemoCustomers()) {
                creditDao.insertCustomer(customer)
            }
            for (tx in DemoData.getDemoTransactions()) {
                creditDao.insertTransaction(tx)
            }
            for (closing in DemoData.getDemoClosings()) {
                closingDao.insertClosing(closing)
            }
        }
    }
}

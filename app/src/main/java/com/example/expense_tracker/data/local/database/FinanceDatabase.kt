package com.example.expense_tracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expense_tracker.data.local.dao.BudgetDao
import com.example.expense_tracker.data.local.dao.ExpenseCategoryDao
import com.example.expense_tracker.data.local.dao.ExpenseDao
import com.example.expense_tracker.data.local.dao.LoanDao
import com.example.expense_tracker.data.model.BudgetEntity
import com.example.expense_tracker.data.model.ExpenseCategoryEntity
import com.example.expense_tracker.data.model.ExpenseEntity
import com.example.expense_tracker.data.model.LoanEntity

@Database(
    entities = [
        ExpenseEntity::class,
        ExpenseCategoryEntity::class,
        BudgetEntity::class,
        LoanEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun loanDao(): LoanDao

    companion object {
        private var instance: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                ).build().also { instance = it }
            }
        }
    }
}

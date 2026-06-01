package com.example.expense_tracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expense_tracker.data.local.dao.BudgetDao
import com.example.expense_tracker.data.local.dao.ExpenseCategoryDao
import com.example.expense_tracker.data.local.dao.ExpenseDao
import com.example.expense_tracker.data.local.dao.LoanDao
import com.example.expense_tracker.data.model.BudgetEntity
import com.example.expense_tracker.data.model.ExpenseCategoryEntity
import com.example.expense_tracker.data.model.ExpenseEntity
import com.example.expense_tracker.data.model.LoanEntity

// Schemas are exported to app/schemas (see build.gradle). When bumping `version`,
// add a Migration in DatabaseModule and a matching schema JSON rather than destructively recreating.
@Database(
    entities = [
        ExpenseEntity::class,
        ExpenseCategoryEntity::class,
        BudgetEntity::class,
        LoanEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun loanDao(): LoanDao
}

package com.example.expense_tracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expense_tracker.data.local.dao.InvestmentDao
import com.example.expense_tracker.data.local.dao.LoanDao
import com.example.expense_tracker.data.model.InvestmentEntity
import com.example.expense_tracker.data.model.LoanEntity

// Schemas are exported to app/schemas (see build.gradle). When bumping `version`,
// add a Migration in DatabaseModule and a matching schema JSON rather than destructively recreating.
// PRD revision: v4 drops expense/category/budget tables (out of scope) and reworks loans for EMI auto-update
@Database(
    entities = [
        LoanEntity::class,
        InvestmentEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao
    abstract fun investmentDao(): InvestmentDao
}

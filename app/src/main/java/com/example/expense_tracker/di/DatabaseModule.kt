package com.example.expense_tracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expense_tracker.data.local.database.FinanceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // v2 adds interest_rate and emi to the loans table to power repayment suggestions.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE loans ADD COLUMN interest_rate REAL NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE loans ADD COLUMN emi REAL NOT NULL DEFAULT 0")
        }
    }

    // v3 adds the investments table for investment tracking
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS investments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    asset_class TEXT NOT NULL,
                    invested_amount REAL NOT NULL,
                    current_value REAL NOT NULL,
                    date TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    // PRD revision: v4 drops expenses/expense_categories/budgets (removed from scope) and rebuilds
    // loans with emi_amount, loan_start_date, emi_day_of_month, last_balance_update_date,
    // backfilling loan_start_date/last_balance_update_date from created_at and emi_amount from emi.
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS expenses")
            db.execSQL("DROP TABLE IF EXISTS expense_categories")
            db.execSQL("DROP TABLE IF EXISTS budgets")

            db.execSQL(
                """
                CREATE TABLE loans_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    current_balance REAL NOT NULL,
                    interest_rate REAL NOT NULL,
                    emi_amount REAL NOT NULL,
                    loan_start_date TEXT NOT NULL,
                    emi_day_of_month INTEGER NOT NULL,
                    last_balance_update_date TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO loans_new (
                    id, name, current_balance, interest_rate, emi_amount,
                    loan_start_date, emi_day_of_month, last_balance_update_date, created_at
                )
                SELECT
                    id, name, current_balance,
                    CASE WHEN interest_rate > 0 THEN interest_rate ELSE 0.01 END,
                    CASE WHEN emi > 0 THEN emi ELSE 1 END,
                    created_at,
                    CAST(strftime('%d', created_at) AS INTEGER),
                    created_at,
                    created_at
                FROM loans
                """.trimIndent()
            )
            db.execSQL("DROP TABLE loans")
            db.execSQL("ALTER TABLE loans_new RENAME TO loans")
        }
    }

    @Singleton
    @Provides
    fun provideFinanceDatabase(
        @ApplicationContext context: Context
    ): FinanceDatabase {
        return Room.databaseBuilder(
            context,
            FinanceDatabase::class.java,
            "finance_database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }
}

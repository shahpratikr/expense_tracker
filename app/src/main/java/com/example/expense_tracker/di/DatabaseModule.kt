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

    // R-4: v3 adds the investments table for investment tracking
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

    @Singleton
    @Provides
    fun provideFinanceDatabase(
        @ApplicationContext context: Context
    ): FinanceDatabase {
        return Room.databaseBuilder(
            context,
            FinanceDatabase::class.java,
            "finance_database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }
}

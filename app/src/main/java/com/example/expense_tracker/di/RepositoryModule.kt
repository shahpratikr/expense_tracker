package com.example.expense_tracker.di

import com.example.expense_tracker.data.local.database.FinanceDatabase
import com.example.expense_tracker.data.local.repository.BudgetRepository
import com.example.expense_tracker.data.local.repository.ExpenseCategoryRepository
import com.example.expense_tracker.data.local.repository.ExpenseRepository
import com.example.expense_tracker.data.local.repository.LoanRepository
import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import com.example.expense_tracker.domain.repository.ILoanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideExpenseRepository(database: FinanceDatabase): IExpenseRepository {
        return ExpenseRepository(database.expenseDao())
    }

    @Singleton
    @Provides
    fun provideExpenseCategoryRepository(database: FinanceDatabase): IExpenseCategoryRepository {
        return ExpenseCategoryRepository(database.expenseCategoryDao())
    }

    @Singleton
    @Provides
    fun provideBudgetRepository(database: FinanceDatabase): IBudgetRepository {
        return BudgetRepository(database.budgetDao())
    }

    @Singleton
    @Provides
    fun provideLoanRepository(database: FinanceDatabase): ILoanRepository {
        return LoanRepository(database.loanDao())
    }
}

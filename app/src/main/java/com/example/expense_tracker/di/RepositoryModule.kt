package com.example.expense_tracker.di

import com.example.expense_tracker.data.local.database.FinanceDatabase
import com.example.expense_tracker.data.local.repository.InvestmentRepository
import com.example.expense_tracker.data.local.repository.LoanRepository
import com.example.expense_tracker.domain.repository.IInvestmentRepository
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
    fun provideLoanRepository(database: FinanceDatabase): ILoanRepository {
        return LoanRepository(database.loanDao())
    }

    @Singleton
    @Provides
    fun provideInvestmentRepository(database: FinanceDatabase): IInvestmentRepository {
        return InvestmentRepository(database.investmentDao())
    }
}

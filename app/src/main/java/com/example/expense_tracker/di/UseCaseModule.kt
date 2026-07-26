package com.example.expense_tracker.di

import com.example.expense_tracker.domain.repository.IInvestmentRepository
import com.example.expense_tracker.domain.repository.ILoanRepository
import com.example.expense_tracker.domain.usecase.investment.AddInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.DeleteInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.EditInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentsUseCase
import com.example.expense_tracker.domain.usecase.investment.UpdateInvestmentValueUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetTotalLoanBalanceUseCase
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
import com.example.expense_tracker.domain.usecase.loan.RecalculateLoanBalancesUseCase
import com.example.expense_tracker.domain.usecase.loan.UpdateLoanBalanceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideGetLoansUseCase(repository: ILoanRepository): GetLoansUseCase {
        return GetLoansUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddLoanUseCase(repository: ILoanRepository): AddLoanUseCase {
        return AddLoanUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideEditLoanUseCase(repository: ILoanRepository): EditLoanUseCase {
        return EditLoanUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteLoanUseCase(repository: ILoanRepository): DeleteLoanUseCase {
        return DeleteLoanUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateLoanBalanceUseCase(repository: ILoanRepository): UpdateLoanBalanceUseCase {
        return UpdateLoanBalanceUseCase(repository)
    }

    // PRD Feature 1: auto-recalculation use case, invoked by LoanViewModel on screen load
    @Singleton
    @Provides
    fun provideRecalculateLoanBalancesUseCase(repository: ILoanRepository): RecalculateLoanBalancesUseCase {
        return RecalculateLoanBalancesUseCase(repository)
    }

    // PRD Feature 3: Dashboard use case — total outstanding loan balance
    @Singleton
    @Provides
    fun provideGetTotalLoanBalanceUseCase(repository: ILoanRepository): GetTotalLoanBalanceUseCase {
        return GetTotalLoanBalanceUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetInvestmentsUseCase(repository: IInvestmentRepository): GetInvestmentsUseCase {
        return GetInvestmentsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddInvestmentUseCase(repository: IInvestmentRepository): AddInvestmentUseCase {
        return AddInvestmentUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideEditInvestmentUseCase(repository: IInvestmentRepository): EditInvestmentUseCase {
        return EditInvestmentUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteInvestmentUseCase(repository: IInvestmentRepository): DeleteInvestmentUseCase {
        return DeleteInvestmentUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateInvestmentValueUseCase(repository: IInvestmentRepository): UpdateInvestmentValueUseCase {
        return UpdateInvestmentValueUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetInvestmentGainLossUseCase(repository: IInvestmentRepository): GetInvestmentGainLossUseCase {
        return GetInvestmentGainLossUseCase(repository)
    }
}

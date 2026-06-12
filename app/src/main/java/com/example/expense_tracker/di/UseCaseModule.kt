package com.example.expense_tracker.di

import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import com.example.expense_tracker.domain.repository.ILoanRepository
import com.example.expense_tracker.domain.usecase.investment.AddInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.DeleteInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.EditInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentsByAssetClassUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentsUseCase
import com.example.expense_tracker.domain.usecase.investment.UpdateInvestmentValueUseCase
import com.example.expense_tracker.domain.usecase.budget.AddBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.DeleteBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.EditBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetsUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetWithSpentUseCase
import com.example.expense_tracker.domain.usecase.category.AddCategoryUseCase
import com.example.expense_tracker.domain.usecase.category.CreatePredefinedCategoriesUseCase
import com.example.expense_tracker.domain.usecase.category.DeleteCategoryUseCase
import com.example.expense_tracker.domain.usecase.category.GetCategoriesUseCase
import com.example.expense_tracker.domain.usecase.category.RenameCategoryUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetBudgetHeadroomUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetMonthlySpendingUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetTotalLoanBalanceUseCase
import com.example.expense_tracker.domain.usecase.expense.AddExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.DeleteExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.EditExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.FilterExpensesByDateUseCase
import com.example.expense_tracker.domain.usecase.expense.GetExpenseByIdUseCase
import com.example.expense_tracker.domain.usecase.expense.GetExpensesUseCase
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GenerateRepaymentIdeasUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
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
    fun provideGetExpensesUseCase(repository: IExpenseRepository): GetExpensesUseCase {
        return GetExpensesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddExpenseUseCase(repository: IExpenseRepository): AddExpenseUseCase {
        return AddExpenseUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetExpenseByIdUseCase(repository: IExpenseRepository): GetExpenseByIdUseCase {
        return GetExpenseByIdUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideEditExpenseUseCase(repository: IExpenseRepository): EditExpenseUseCase {
        return EditExpenseUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteExpenseUseCase(repository: IExpenseRepository): DeleteExpenseUseCase {
        return DeleteExpenseUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideFilterExpensesByDateUseCase(repository: IExpenseRepository): FilterExpensesByDateUseCase {
        return FilterExpensesByDateUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetCategoriesUseCase(repository: IExpenseCategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddCategoryUseCase(repository: IExpenseCategoryRepository): AddCategoryUseCase {
        return AddCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideCreatePredefinedCategoriesUseCase(repository: IExpenseCategoryRepository): CreatePredefinedCategoriesUseCase {
        return CreatePredefinedCategoriesUseCase(repository)
    }

    // R-1: Category rename and delete use cases
    @Singleton
    @Provides
    fun provideDeleteCategoryUseCase(repository: IExpenseCategoryRepository): DeleteCategoryUseCase {
        return DeleteCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideRenameCategoryUseCase(repository: IExpenseCategoryRepository): RenameCategoryUseCase {
        return RenameCategoryUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetBudgetsUseCase(repository: IBudgetRepository): GetBudgetsUseCase {
        return GetBudgetsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddBudgetUseCase(repository: IBudgetRepository): AddBudgetUseCase {
        return AddBudgetUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideEditBudgetUseCase(repository: IBudgetRepository): EditBudgetUseCase {
        return EditBudgetUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteBudgetUseCase(repository: IBudgetRepository): DeleteBudgetUseCase {
        return DeleteBudgetUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetBudgetWithSpentUseCase(
        budgetRepository: IBudgetRepository,
        expenseRepository: IExpenseRepository
    ): GetBudgetWithSpentUseCase {
        return GetBudgetWithSpentUseCase(budgetRepository, expenseRepository)
    }

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

    @Singleton
    @Provides
    fun provideGetMonthlySpendingUseCase(repository: IExpenseRepository): GetMonthlySpendingUseCase {
        return GetMonthlySpendingUseCase(repository)
    }

    // R-5: Dashboard use case — total outstanding loan balance
    @Singleton
    @Provides
    fun provideGetTotalLoanBalanceUseCase(repository: ILoanRepository): GetTotalLoanBalanceUseCase {
        return GetTotalLoanBalanceUseCase(repository)
    }

    // R-5: Dashboard use case — remaining budget headroom for current month
    @Singleton
    @Provides
    fun provideGetBudgetHeadroomUseCase(
        budgetRepository: IBudgetRepository,
        expenseRepository: IExpenseRepository
    ): GetBudgetHeadroomUseCase {
        return GetBudgetHeadroomUseCase(budgetRepository, expenseRepository)
    }

    @Singleton
    @Provides
    fun provideGenerateRepaymentIdeasUseCase(): GenerateRepaymentIdeasUseCase {
        return GenerateRepaymentIdeasUseCase()
    }

    // R-4: Investment use case providers
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
    fun provideGetInvestmentsByAssetClassUseCase(repository: IInvestmentRepository): GetInvestmentsByAssetClassUseCase {
        return GetInvestmentsByAssetClassUseCase(repository)
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

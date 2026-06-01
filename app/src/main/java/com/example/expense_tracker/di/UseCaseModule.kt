package com.example.expense_tracker.di

import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import com.example.expense_tracker.domain.repository.ILoanRepository
import com.example.expense_tracker.domain.usecase.budget.AddBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.DeleteBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.EditBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetsUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetWithSpentUseCase
import com.example.expense_tracker.domain.usecase.category.AddCategoryUseCase
import com.example.expense_tracker.domain.usecase.category.CreatePredefinedCategoriesUseCase
import com.example.expense_tracker.domain.usecase.category.GetCategoriesUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetMonthlySpendingUseCase
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

    @Singleton
    @Provides
    fun provideGenerateRepaymentIdeasUseCase(): GenerateRepaymentIdeasUseCase {
        return GenerateRepaymentIdeasUseCase()
    }
}

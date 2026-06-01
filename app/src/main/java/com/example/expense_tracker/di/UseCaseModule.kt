package com.example.expense_tracker.di

import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import com.example.expense_tracker.domain.usecase.category.AddCategoryUseCase
import com.example.expense_tracker.domain.usecase.category.CreatePredefinedCategoriesUseCase
import com.example.expense_tracker.domain.usecase.category.GetCategoriesUseCase
import com.example.expense_tracker.domain.usecase.expense.AddExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.DeleteExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.EditExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.FilterExpensesByDateUseCase
import com.example.expense_tracker.domain.usecase.expense.GetExpensesUseCase
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
}

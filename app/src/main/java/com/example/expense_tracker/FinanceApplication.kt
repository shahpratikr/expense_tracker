package com.example.expense_tracker

import android.app.Application
import com.example.expense_tracker.domain.usecase.category.CreatePredefinedCategoriesUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FinanceApplication : Application() {

    @Inject
    lateinit var createPredefinedCategoriesUseCase: CreatePredefinedCategoriesUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            createPredefinedCategoriesUseCase()
        }
    }
}

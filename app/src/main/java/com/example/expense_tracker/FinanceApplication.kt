package com.example.expense_tracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// PRD: Application entry point — predefined category bootstrap removed with expense/category scope
@HiltAndroidApp
class FinanceApplication : Application()

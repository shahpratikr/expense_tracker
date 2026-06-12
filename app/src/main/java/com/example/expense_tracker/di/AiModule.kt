package com.example.expense_tracker.di

import android.content.Context
import com.example.expense_tracker.ai.ILlmInferenceHelper
import com.example.expense_tracker.ai.LlmInferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// R-3: Hilt module providing the LlmInferenceHelper as an application-scoped singleton
//      so the model is not reloaded on recomposition
@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    // R-3: Provides ILlmInferenceHelper bound to LlmInferenceHelper; singleton to preserve LLM state
    @Singleton
    @Provides
    fun provideLlmInferenceHelper(
        @ApplicationContext context: Context
    ): ILlmInferenceHelper {
        return LlmInferenceHelper(context)
    }
}

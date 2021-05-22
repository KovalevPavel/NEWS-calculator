package com.github.newscalculator.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления контекста
 */
@Module
class ContextModule (private val context: Context) {
    @Provides
    fun provideContext() = context
}
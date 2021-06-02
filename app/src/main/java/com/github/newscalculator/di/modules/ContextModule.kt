package com.github.newscalculator.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления контекста
 */
@Module
class ContextModule (private val application: Application) {

    @Provides
    fun provideApplication() = application
}
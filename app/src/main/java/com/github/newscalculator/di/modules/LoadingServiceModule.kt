package com.github.newscalculator.di.modules

import android.content.Context
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.data.local.ParametersLoadLocal
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления компонентов, отвечающих за загрузку данных
 */
@Module
class LoadingServiceModule {
    @Provides
    fun provideLoadingService(context: Context): LoadParametersService {
        return ParametersLoadLocal(context)
    }
}
package com.github.newscalculator.di.modules

import android.app.Application
import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления различных UseCase
 */
@Module
class UseCaseModule {
    @Provides
    fun provideNEWSEntityUseCase() = DiseaseTypeUseCase()

    @Provides
    fun provideSPrefsUseCase(context: Application) = SharedPrefsUseCase(context)
}
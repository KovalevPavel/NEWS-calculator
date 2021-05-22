package com.github.newscalculator.di.modules

import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления различных UseCase
 */
@Module
class UseCaseModule {
    @Provides
    fun provideNEWSEntityUseCase() = DiseaseTypeUseCase()
}
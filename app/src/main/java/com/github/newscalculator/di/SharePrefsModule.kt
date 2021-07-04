package com.github.newscalculator.di

import com.github.newscalculator.data.ComputationRepository
import com.github.newscalculator.domain.usecases.ComputationUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Модуль предоставления различных UseCase
 */
@Module
@InstallIn (ViewModelComponent::class)
class SharePrefsModule {
    @Provides
    fun provideNEWSEntityUseCase(compRepo: ComputationRepository) = ComputationUseCaseImpl(compRepo)

}
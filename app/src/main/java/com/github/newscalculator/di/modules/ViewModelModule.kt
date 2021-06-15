package com.github.newscalculator.di.modules

import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.usecases.MainViewModel
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Модуль предоставления viewModel
 */
@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun getMainViewModel(
        loadService: LoadParametersService,
        sharedPrefsUseCase: SharedPrefsUseCase
    ): MainViewModel {
        return MainViewModel(loadService, sharedPrefsUseCase)
    }
}
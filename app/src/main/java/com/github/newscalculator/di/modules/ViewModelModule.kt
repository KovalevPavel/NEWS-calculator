package com.github.newscalculator.di.modules

import android.app.Application
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.usecases.MainViewModel
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
    fun getMainViewModel(loadService: LoadParametersService, application: Application): MainViewModel {
        return MainViewModel(loadService, application)
    }
}
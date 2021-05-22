package com.github.newscalculator.di

import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.di.modules.ContextModule
import com.github.newscalculator.di.modules.LoadingServiceModule
import com.github.newscalculator.di.modules.UseCaseModule
import com.github.newscalculator.di.modules.ViewModelModule
import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import com.github.newscalculator.ui.mainFragment.MainViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Компонент Dagger
 */
@Component(
    modules = [
        ContextModule::class,
        LoadingServiceModule::class,
        ViewModelModule::class,
        UseCaseModule::class
    ]
)
@Singleton
interface AppComponent {
    fun getLoadingService(): LoadParametersService
    fun getMainViewModel(): MainViewModel
    fun getNEWSUseCase(): DiseaseTypeUseCase
}
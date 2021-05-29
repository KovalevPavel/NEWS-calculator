package com.github.newscalculator.di

import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.di.modules.*
import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import com.github.newscalculator.domain.usecases.HandleLocalParametersListUseCase
import com.github.newscalculator.ui.mainFragment.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
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
        UseCaseModule::class,
        DatabaseModule::class
    ]
)
@Singleton
interface AppComponent {
    fun getLoadingService(): LoadParametersService
    fun getMainViewModel(): MainViewModel
    fun getNEWSUseCase(): DiseaseTypeUseCase
    fun getDatabaseReference(): FirebaseFirestore
    fun getLocalFilesUseCase(): HandleLocalParametersListUseCase
}
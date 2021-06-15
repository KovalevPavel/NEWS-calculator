package com.github.newscalculator.di

import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.data.NavigationDrawerInteraction
import com.github.newscalculator.di.modules.*
import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import com.github.newscalculator.domain.usecases.MainViewModel
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
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
        DatabaseModule::class,
        NavigationDrawerModule::class
    ]
)
@Singleton
interface AppComponent {
    fun getLoadingService(): LoadParametersService
    fun getSharedPrefsUseCase(): SharedPrefsUseCase
    fun getMainViewModel(): MainViewModel
    fun getNEWSUseCase(): DiseaseTypeUseCase
    fun getDatabaseReference(): FirebaseFirestore
    fun getNavigationDrawerInteraction(): NavigationDrawerInteraction
}
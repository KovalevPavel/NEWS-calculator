package com.github.newscalculator.di.modules

import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.data.remote.ParametersLoadRemote
import com.github.newscalculator.domain.usecases.HandleLocalParametersListUseCase
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставления компонентов, отвечающих за загрузку данных
 */
@Module
class LoadingServiceModule {
    @Provides
    fun provideLoadingService(
        db: FirebaseFirestore,
        sPrefsUseCase: SharedPrefsUseCase,
        localFilesUseCase: HandleLocalParametersListUseCase
    ): LoadParametersService {
        return ParametersLoadRemote(db, sPrefsUseCase, localFilesUseCase)
    }
}
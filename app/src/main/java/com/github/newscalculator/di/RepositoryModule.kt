package com.github.newscalculator.di

import android.app.Application
import com.github.newscalculator.data.loadingData.LoadParametersRepository
import com.github.newscalculator.data.loadingData.local.LoadParametersRepositoryLocal
import com.github.newscalculator.data.loadingData.remote.LoadParametersRepositoryRemote
import com.github.newscalculator.data.qualifiers.LocalRepository
import com.github.newscalculator.data.qualifiers.RemoteRepository
import com.github.newscalculator.data.sharePrefs.SharePrefsRepository
import com.github.newscalculator.data.sharePrefs.SharePrefsRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Модуль предоставления репозиториев, отвечающих за загрузку данных
 */
@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    fun provideSharePrefsRepository(context: Application): SharePrefsRepository {
        return SharePrefsRepositoryImpl(context)
    }

    @Provides
    @RemoteRepository
    fun provideLoadRepositoryRemote(
        db: FirebaseFirestore,
        sPrefsRepo: SharePrefsRepository,
        context: Application
    ): LoadParametersRepository {
        return LoadParametersRepositoryRemote(db, sPrefsRepo, context)
    }

    @Provides
    @LocalRepository
    fun provideLoadRepositoryLocal(): LoadParametersRepository {
        return LoadParametersRepositoryLocal()
    }
}
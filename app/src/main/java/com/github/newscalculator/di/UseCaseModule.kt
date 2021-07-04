package com.github.newscalculator.di

import com.github.newscalculator.data.ComputationRepository
import com.github.newscalculator.data.NavigationDrawerRepository
import com.github.newscalculator.data.loadingData.LoadParametersRepository
import com.github.newscalculator.data.qualifiers.LocalRepository
import com.github.newscalculator.data.sharePrefs.SharePrefsRepository
import com.github.newscalculator.domain.usecases.ComputationUseCaseImpl
import com.github.newscalculator.domain.usecases.LoadingUseCaseImpl
import com.github.newscalculator.domain.usecases.NavigationDrawerUseCaseImpl
import com.github.newscalculator.domain.usecases.SharedPrefsUseCaseImpl
import com.github.newscalculator.domain.usecases.interfaces.ComputationUseCase
import com.github.newscalculator.domain.usecases.interfaces.LoadingUseCase
import com.github.newscalculator.domain.usecases.interfaces.NavigationDrawerUseCase
import com.github.newscalculator.domain.usecases.interfaces.SharedPrefsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    fun provideSharedPrefsUseCase(
        sPrefsRepo: SharePrefsRepository
    ): SharedPrefsUseCase {
        return SharedPrefsUseCaseImpl(sPrefsRepo)
    }

    @Provides
    fun provideDiseaseTypeUseCase(
        evalRepository: ComputationRepository
    ): ComputationUseCase {
        return ComputationUseCaseImpl(evalRepository)
    }

    @Provides
    fun provideNavigationDrawerUseCase(drawerRepo: NavigationDrawerRepository): NavigationDrawerUseCase {
        return NavigationDrawerUseCaseImpl(drawerRepo)
    }

    @Provides
    fun provideLoadingUseCase(@LocalRepository loadingRepo: LoadParametersRepository): LoadingUseCase {
        return LoadingUseCaseImpl(loadingRepo)
    }
}
package com.github.newscalculator.di.modules

import com.github.newscalculator.data.NavigationDrawerInteraction
import com.github.newscalculator.data.remote.NavigationDrawerInteractionImpl
import dagger.Module
import dagger.Provides

@Module
class NavigationDrawerModule {
    @Provides
    fun provideNavigationDrawerInteraction(): NavigationDrawerInteraction {
        return NavigationDrawerInteractionImpl()
    }
}
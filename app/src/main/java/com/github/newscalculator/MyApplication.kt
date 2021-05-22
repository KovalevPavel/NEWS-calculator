package com.github.newscalculator

import android.app.Application
import com.github.newscalculator.di.AppComponent
import com.github.newscalculator.di.DaggerAppComponent
import com.github.newscalculator.di.modules.ContextModule

class MyApplication: Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {
        super.onCreate()
        initializeDagger()
    }

    private fun initializeDagger() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }
}
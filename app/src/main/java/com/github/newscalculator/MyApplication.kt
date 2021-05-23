package com.github.newscalculator

import android.app.Application
import com.github.newscalculator.di.AppComponent
import com.github.newscalculator.di.DaggerAppComponent
import com.github.newscalculator.di.modules.ContextModule
import com.github.newscalculator.util.loggingDebug
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
        FirebaseApp.initializeApp(this@MyApplication)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result
            loggingDebug("$token")
        }
    }

    private fun initializeDagger() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }
}
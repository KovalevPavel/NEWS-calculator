package com.github.newscalculator.app

import android.app.Application
import android.app.NotificationChannel
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.github.newscalculator.notification.NotificationChannels
import com.github.newscalculator.util.loggingDebug
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        const val COLLECTION_NAME = "newsItems"
    }

    override fun onCreate() {
        super.onCreate()
        initFirebase()
//        создание каналов оповещений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels(this)
    }

    private fun initFirebase() {
        if (BuildConfig.DEBUG) return
        FirebaseApp.initializeApp(this@MyApplication)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result
            loggingDebug("$token")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels(context: Context) {
        val channelList = mutableListOf<NotificationChannel>()
        NotificationChannels.CHANNELS.forEach {
            val channel =
                NotificationChannel(it.channelId, it.channelName, it.channelPriority).apply {
                    enableVibration(true)
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                    description = it.channelDescription
                }
            channelList.add(channel)
        }
        channelList.forEach {
            NotificationManagerCompat.from(context)
                .createNotificationChannel(it)
        }
    }
}
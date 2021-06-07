package com.github.newscalculator.domain.usecases

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPrefsUseCase(private val context: Application) {
    companion object {
        private const val PREFS_NAME = "news_shared_prefs"
        private const val LAST_TIMESTAMP = "last_timestamp"
        private const val DB_VERSION = "dbVersion"
        private const val NETWORK_AVAILABLE = "network"
    }

    suspend fun getLastLaunchTime(): Long {
        return withContext(Dispatchers.IO) {
            getSharedPrefs().getLong(LAST_TIMESTAMP, 0L)
        }
    }

    suspend fun resetUpdateTime() {
        withContext(Dispatchers.IO) {
            getSharedPrefs().edit()
                .putLong(LAST_TIMESTAMP, System.currentTimeMillis())
                .apply()
        }
    }

    suspend fun getCurrentDbVersion(): Long {
        return withContext(Dispatchers.IO) {
            getSharedPrefs().getLong(DB_VERSION, 0L)
        }
    }

    suspend fun updateDbVersion(newDbVersion: Long) {
        withContext(Dispatchers.IO) {
            getSharedPrefs().edit()
                .putLong(DB_VERSION, newDbVersion)
                .apply()
        }
    }

    suspend fun getNetworkState(): Boolean {
        return withContext(Dispatchers.IO) {
            getSharedPrefs().getBoolean(NETWORK_AVAILABLE, false)
        }
    }

    suspend fun updateNetworkState(state: Boolean) {
        withContext(Dispatchers.IO) {
            getSharedPrefs().edit()
                .putBoolean(NETWORK_AVAILABLE, state)
                .apply()
        }
    }

    private fun getSharedPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
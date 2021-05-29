package com.github.newscalculator.domain.usecases

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPrefsUseCase(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "news_shared_prefs"
        private const val LAST_TIMESTAMP = "last_timestamp"
        private const val DB_VERSION = "dbVersion"
    }

    suspend fun getLastLaunchTime(): Long {
        return withContext(Dispatchers.IO) {
            getSharedPrefs().getLong(LAST_TIMESTAMP, 0L)
        }
    }

    suspend fun updateLaunchTime() {
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

    fun updateDbVersion(newDbVersion: Long) {
        getSharedPrefs().edit()
            .putLong(DB_VERSION, newDbVersion)
            .apply()
    }

    private fun getSharedPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
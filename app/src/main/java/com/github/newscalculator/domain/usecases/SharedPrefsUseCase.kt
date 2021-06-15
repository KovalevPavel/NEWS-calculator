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
        private const val HELLO_DIALOG_SHOWED = "hello_dialog"
    }

    private fun getSharedPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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

    suspend fun getHelloDialogShowed(): Boolean {
        return withContext(Dispatchers.IO) {
            val dialogShowed = getSharedPrefs().getBoolean(HELLO_DIALOG_SHOWED, false)
            if (!dialogShowed) setHelloDialogShowed(true)
            dialogShowed
        }
    }

    private suspend fun setHelloDialogShowed(showed: Boolean) {
        withContext(Dispatchers.IO) {
            getSharedPrefs().edit()
                .putBoolean(HELLO_DIALOG_SHOWED, showed)
                .apply()
        }
    }
}
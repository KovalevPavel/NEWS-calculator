package com.github.newscalculator.data.sharePrefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SharePrefsRepositoryImpl @Inject constructor(
    private val context: Context
): SharePrefsRepository {
    companion object {
        private const val PREFS_NAME = "news_shared_prefs"
        private const val LAST_TIMESTAMP = "last_timestamp"
        private const val HELLO_DIALOG_SHOWED = "hello_dialog"
    }

    private fun getSharedPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun getLastLaunchTime(): Long {
        return withContext(Dispatchers.IO) {
            getSharedPrefs().getLong(LAST_TIMESTAMP, 0L)
        }
    }

    override suspend fun resetUpdateTime() {
        withContext(Dispatchers.IO) {
            getSharedPrefs().edit()
                .putLong(LAST_TIMESTAMP, System.currentTimeMillis())
                .apply()
        }
    }

    override suspend fun getHelloDialogShowed(): Boolean {
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
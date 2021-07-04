package com.github.newscalculator.data.sharePrefs

interface SharePrefsRepository {
    suspend fun getLastLaunchTime(): Long
    suspend fun resetUpdateTime()
    suspend fun getHelloDialogShowed(): Boolean
}
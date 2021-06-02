package com.github.newscalculator.network

import android.net.ConnectivityManager
import android.net.Network
import com.github.newscalculator.MyApplication
import com.github.newscalculator.util.loggingDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Колбэк, который при отработке изменяет запись о доступности сети в SharedPreferences
 */
class MyNetworkCallback: ConnectivityManager.NetworkCallback() {
    private val sPrefsUseCase = MyApplication.appComponent.getSharedPrefsUseCase()
    override fun onAvailable(network: Network) {
        CoroutineScope(Dispatchers.IO).launch {
            loggingDebug("got it!")
            sPrefsUseCase.updateNetworkState(true)
        }
    }

    override fun onUnavailable() {
        CoroutineScope(Dispatchers.IO).launch {
            loggingDebug("not available")
            sPrefsUseCase.updateNetworkState(false)
        }
    }

    override fun onLost(network: Network) {
        CoroutineScope(Dispatchers.IO).launch {
            loggingDebug("lost")
            sPrefsUseCase.updateNetworkState(false)
        }
    }
}
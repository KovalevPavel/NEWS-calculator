package com.github.newscalculator.util

import android.util.Log
import com.github.newscalculator.BuildConfig

fun loggingDebug(string: String) {
    if (BuildConfig.DEBUG)
        Log.d("loggingDebug", string)
}
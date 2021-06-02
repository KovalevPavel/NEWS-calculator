package com.github.newscalculator.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.github.newscalculator.BuildConfig

fun loggingDebug(string: String) {
    if (BuildConfig.DEBUG)
        Log.d("loggingDebug", string)
}

fun showToast (context: Context, string: String) {
    Toast.makeText (context, string, Toast.LENGTH_LONG).show()
}
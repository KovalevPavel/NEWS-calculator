package com.github.newscalculator.util

import android.text.Editable
import android.util.Log
import com.github.newscalculator.BuildConfig

fun Double.truncation(): Double {
    val dotPosition = this.toString().toCharArray().indexOfFirst {
        it == '.'
    }
    return this.toString().substring(0, dotPosition + 2).toDouble()
}

fun logDebug(string: String) {
    if (BuildConfig.DEBUG)
        Log.d("LoggingDebug", string)
}

fun Editable.containsWrongChars(charArray: Array<String>): Boolean {
    charArray.forEach {
        if (this.toString() == it) return true
    }
    return false
}
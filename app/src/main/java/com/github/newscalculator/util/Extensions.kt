package com.github.newscalculator.util

import android.text.Editable

fun Double.truncation(): Double {
    val dotPosition = this.toString().toCharArray().indexOfFirst {
        it == '.'
    }
    return this.toString().substring(0, dotPosition + 2).toDouble()
}

fun Editable.containsWrongChars(charArray: Array<String>): Boolean {
    charArray.forEach {
        if (this.toString() == it) return true
    }
    return false
}
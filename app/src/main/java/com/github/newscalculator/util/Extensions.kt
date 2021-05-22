package com.github.newscalculator.util

import android.text.Editable

/**
 * Файл с различными расширениями.
 */

/**
 * Убирает дробную часть [Double].
 * @return число в формате [Double] без дробной части.
 */
fun Double.truncation(): Double {
    val dotPosition = this.toString().toCharArray().indexOfFirst {
        it == '.'
    }
    return this.toString().substring(0, dotPosition + 2).toDouble()
}

/**
 * Опрделяет, содержит ли поле ввода недопустимые значения.
 * @param charArray - массив из нежелательных символов.
 * @return <code>true</code> если в текстовом поле содержатся нежелательные символы и <code>false</code> если таких нет.
 */
fun Editable.containsWrongChars(charArray: Array<String>): Boolean {
    charArray.forEach {
        if (this.toString() == it) return true
    }
    return false
}
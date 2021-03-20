package com.github.newscalculator.screens.mainfragment

import com.github.newscalculator.EvalParameter

interface ConnectionToDialog {
    var allowToCallDialog: Boolean
    fun onDialogClicked(evalParameter: EvalParameter, measuredValue: Double?, measuredIsChecked: Boolean = false)
}
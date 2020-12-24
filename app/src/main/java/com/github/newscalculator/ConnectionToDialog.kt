package com.github.newscalculator

interface ConnectionToDialog {
    var allowToCallDialog: Boolean
    fun onDialogClicked(evalParameter: EvalParameter, measuredValue: Double?, measuredIsChecked: Boolean = false)
}
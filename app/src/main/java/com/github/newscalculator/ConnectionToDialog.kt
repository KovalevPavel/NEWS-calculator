package com.github.newscalculator

interface ConnectionToDialog {
    fun onDialogClicked(evalParameter: EvalParameter, measuredValue: Double?, measuredIsChecked: Boolean = false)
}
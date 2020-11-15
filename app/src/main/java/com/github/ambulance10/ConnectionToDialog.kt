package com.github.ambulance10

interface ConnectionToDialog {
    fun onDialogClicked(evalParameter: EvalParameter, evalDiseasePoints: Double, evalCheck: Boolean = false)
}
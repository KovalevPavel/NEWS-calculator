package com.github.newscalculator.screens.mainfragment

import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

interface ConnectionToDialog {
    var allowToCallDialog: Boolean
    fun onDialogClicked(diseaseParameter: AbstractDiseaseType, measuredValue: Double, measuredIsChecked: Boolean = false)
}
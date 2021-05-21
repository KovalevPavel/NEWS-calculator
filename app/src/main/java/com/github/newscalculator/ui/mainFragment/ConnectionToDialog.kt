package com.github.newscalculator.ui.mainFragment

import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

interface ConnectionToDialog {
    var allowToCallDialog: Boolean
    fun onDialogClicked(diseaseParameter: AbstractDiseaseType, measuredValue: Double, measuredIsChecked: Boolean = false)
}
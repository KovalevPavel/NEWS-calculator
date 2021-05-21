package com.github.newscalculator.ui.editvaluedialog

import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

class EditValueRepository {

    fun convertEvalValue(item: AbstractDiseaseType): String {
        val numberParameter = item.getNumberParameter
        return when (item.fractional) {
            true -> if (numberParameter == -1.0) "" else "$numberParameter"
            false -> if (numberParameter == -1.0) "" else "${numberParameter.toInt()}"
        }
    }
}
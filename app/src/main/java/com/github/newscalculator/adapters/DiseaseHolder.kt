package com.github.newscalculator.adapters

import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

class DiseaseHolder(
    private val binder: ItemEvaluationParameterBinding,
    onClick: (Int) -> Unit
) : BaseHolder(binder.root, onClick) {

    fun bind(item: AbstractDiseaseType) {
        binder.apply {
            textParameterName.text = item.parameterName
            textViewEvalPoints.text = item.createMeasuredString()
            textViewDiseasePoints.text = item.createPointsString()
        }
    }
}
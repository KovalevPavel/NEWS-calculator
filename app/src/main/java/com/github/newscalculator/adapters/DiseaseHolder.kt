package com.github.newscalculator.adapters

import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CombinedDiseaseType

class DiseaseHolder(
    private val binder: ItemEvaluationParameterBinding,
    onClick: (Int) -> Unit
) : BaseHolder(binder.root, onClick) {

    fun bind(item: AbstractDiseaseType) {
        binder.apply {
            textParameterName.text = item.parameterName
            textViewEvalPoints.text = makeEvalString(item)
            textViewDiseasePoints.text = item.createPointsString()
        }
    }

    private fun makeEvalString(item: AbstractDiseaseType) =
        when (item.measuredArray[0]) {
            0 ->
                if (item.measuredArray[1] as Boolean) item.shortString
                else ""
            else -> convertEvalValue(item)
        }

    private fun convertEvalValue(item: AbstractDiseaseType): String {
        val measuredValue = item.measuredArray[0] as Double
        return when (item.normalValue) {
            36.6 -> measuredValue.toString()
            else -> if (item is CombinedDiseaseType && item.measuredArray[1] as Boolean)
                "${measuredValue.toInt()}\n${item.shortString}" else "${measuredValue.toInt()}"
        }
    }
}
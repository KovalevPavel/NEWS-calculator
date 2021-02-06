package com.github.newscalculator.adapters

import com.github.newscalculator.EvalParameter
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding

class DiseaseHolder(
    private val binder: ItemEvaluationParameterBinding,
    onClick: (Int) -> Unit
) : BaseHolder(binder.root, onClick) {

    fun bind(item: EvalParameter) {
        binder.apply {
            textParameterName.text = item.parameterName
            textViewEvalPoints.text = makeEvalString(item)

            textViewDiseasePoints.text =
                item.measuredValue?.let {
                    when (item.diseaseBooleanPoints) {
                        0 -> "${item.diseasePoints}"
                        else -> if (item.diseasePoints != 0) "${item.diseasePoints}\n${item.diseaseBooleanPoints}" else "${item.diseaseBooleanPoints}"
                    }
                } ?: "0"
        }
    }

    private fun makeEvalString(item: EvalParameter) =
        when (item.measuredValue) {
            -1.0 ->
                if (item.diseaseBooleanPoints != 0) item.specialMark.substring(0, 5)
                else ""
            else -> convertEvalValue(item)
        }

    private fun convertEvalValue(item: EvalParameter): String {
        var tempString = ""
        item.measuredValue?.let {
            tempString = when (item.normalValue) {
                36.6 -> it.toString()
                else -> if (checkInsufflation(item)) "${it.toInt()}\n${
                    item.specialMark.substring(
                        0,
                        5
                    )
                }" else "${it.toInt()}"
            }
        }
        return tempString
    }

    private fun checkInsufflation(item: EvalParameter) =
        item.id == 1 && item.diseaseBooleanPoints != 0
}
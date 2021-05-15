package com.github.newscalculator.diseaseparameterstypes

import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class CombinedDiseaseType(
    override val id: Long,
    override val parameterName: String,
    override val normalValue: Double,
    override val arrayOfEvalLevels: Array<Double>,
    override val arrayOfDiseasePoints: Array<Int>,
    val maxCheckableValue: Int,
    override val shortString: String,
    override val fractional: Boolean,
    override val required: Boolean,
    //порог, ниже которого переключатель неактивен
    val threshold: Double = 0.0,
) : AbstractDiseaseType(EvalTypes.Combined) {

    override fun evaluatePoints() {
        //ближайший индекс из таблицы NEWS
        val minIndex =
            arrayOfEvalLevels.indexOfFirst {
                arrayOfEvalLevels.indexOf(it) % 2 == 1 &&
                        (getNumberParameter) <= it
            }
        val numericalPoints =
            if (minIndex > 0) arrayOfDiseasePoints[(minIndex - 1) / 2]
            else arrayOfDiseasePoints[arrayOfDiseasePoints.lastIndex]
        val booleanPoints = if (getBooleanParameter) maxCheckableValue else 0

        setMeasuredPoints(numericalPoints)
        setBooleanPoints(booleanPoints)
    }

    override fun createMeasuredString(): String {
        val measuredValue = if (fractional) getNumberParameter else getNumberParameter.toInt()
        return when (getBooleanParameter) {
            true -> "$measuredValue\n${shortString.substring(0 until 5)}"
            false -> if (getNumberParameter > 0.0) "$measuredValue" else ""
        }
    }

    override fun createPointsString(): String {
        return if (!getBooleanParameter) "$getMeasuredPoints" else "$getMeasuredPoints\n$getBooleanPoints"
    }
}
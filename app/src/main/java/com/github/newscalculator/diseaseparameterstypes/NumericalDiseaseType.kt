package com.github.newscalculator.diseaseparameterstypes

import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class NumericalDiseaseType(
    override val id: Long,
    override val parameterName: String,
    override val normalValue: Double,
    override val arrayOfEvalLevels: Array<Double>,
    override val arrayOfDiseasePoints: Array<Int>,
    override val fractional: Boolean,
    override val required: Boolean,
) : AbstractDiseaseType(EvalTypes.Numerical) {

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

        setMeasuredPoints(numericalPoints)
    }

    override fun createMeasuredString(): String {
        val measuredValue = if (fractional) getNumberParameter else getNumberParameter.toInt()
        return if (getNumberParameter > 0.0) "$measuredValue" else ""
    }

    override fun createPointsString() = "${getResultPoints()}"
}
package com.github.newscalculator.domain.entities

import kotlinx.parcelize.Parcelize

@Parcelize
data class NumericalDiseaseType(
    override val id: Long,
    override val parameterName: String,
    override val normalValue: Double,
    override val arrayOfEvalLevels: Array<Double>,
    override val arrayOfDiseasePoints: Array<Int>,
    override val fractional: Boolean,
    override val required: Boolean,
) : AbstractDiseaseType(EvalTypes.NUMERICAL) {

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
    override fun restoreDefault() {
        setMeasuredParameter(-1.0)
        setMeasuredPoints(0)
        isModified = false
    }

    companion object {
        fun convertFromMap(map: MutableMap<String, Any>): AbstractDiseaseType {
            return NumericalDiseaseType(
                id = map[ID_] as Long,
                parameterName = map[PARAM_NAME_] as String,
                normalValue = (map[NORM_VALUE_] as Number).toDouble(),
                arrayOfEvalLevels = (map[ARRAY_LEVELS_] as ArrayList<Number>).convertToDoubleArray(),
                arrayOfDiseasePoints = (map[ARRAY_POINTS_] as ArrayList<Number>).convertToIntArray(),
                fractional = map[FRACTIONAL_] as Boolean,
                required = map[REQUIRED_] as Boolean,
            )
        }
    }
}
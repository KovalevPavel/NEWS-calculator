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
) : AbstractDiseaseType(EvalTypes.Combined) {

    override fun evaluatePoints() {
        val numericalPoints = super.getResultPoints()
        val booleanPoints = if (measuredArray[1] as Boolean) maxCheckableValue else 0
        resultPointsArray[0] = numericalPoints
        resultPointsArray[1] = booleanPoints
    }

    override fun createPointsString(): String {
        return when (resultPointsArray[1]) {
            0 -> super.createPointsString()
            else -> super.createPointsString()+"\n${resultPointsArray[1]}"
        }
    }
}

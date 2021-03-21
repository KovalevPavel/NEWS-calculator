package com.github.newscalculator.diseaseparameterstypes

import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class CheckableDiseaseType(
    override val id: Long,
    override val parameterName: String,
    override val normalValue: Double,
    val maxCheckableValue: Int,
    override val shortString: String,
) : AbstractDiseaseType(EvalTypes.Checkable) {

    override fun evaluatePoints() {
        val booleanPoints = if (measuredArray[1] as Boolean) maxCheckableValue else 0
        resultPointsArray[1] = booleanPoints
    }

    override fun createPointsString(): String = "${resultPointsArray[1]}"
}
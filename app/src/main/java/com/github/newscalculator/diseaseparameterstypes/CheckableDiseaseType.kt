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
    override val fractional: Boolean,
    override val required: Boolean,
) : AbstractDiseaseType(EvalTypes.Checkable) {

    override fun evaluatePoints() {
        val booleanPoints = if (getBooleanParameter) maxCheckableValue else 0
        setBooleanPoints(booleanPoints)
    }

    override fun createMeasuredString() =
        if (getBooleanParameter) shortString.substring(0 until 5) else ""

    override fun createPointsString(): String = "${getResultPoints()}"
}
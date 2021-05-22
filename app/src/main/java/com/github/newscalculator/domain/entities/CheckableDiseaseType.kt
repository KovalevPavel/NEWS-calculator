package com.github.newscalculator.domain.entities

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
) : AbstractDiseaseType(EvalTypes.CHECKABLE) {

    override fun evaluatePoints() {
        val booleanPoints = if (getBooleanParameter) maxCheckableValue else 0
        setBooleanPoints(booleanPoints)
    }

    override fun createMeasuredString() =
        if (getBooleanParameter) shortString.substring(0 until 3) else ""

    override fun createPointsString(): String = "${getResultPoints()}"
    override fun restoreDefault() {
        setBooleanParameter(false)
        setBooleanPoints(0)
        isModified = false
    }
}
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
) : AbstractDiseaseType(EvalTypes.Numerical)


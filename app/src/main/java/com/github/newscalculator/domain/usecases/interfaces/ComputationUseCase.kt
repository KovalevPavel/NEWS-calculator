package com.github.newscalculator.domain.usecases.interfaces

import com.github.newscalculator.domain.entities.AbstractDiseaseType

interface ComputationUseCase {
    fun changeEvalParameter(
        item: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    )

    fun convertEvalValue(item: AbstractDiseaseType): String
}
package com.github.newscalculator.domain.usecases

import com.github.newscalculator.data.ComputationRepository
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.interfaces.ComputationUseCase

/**
 * Операции с объектами [AbstractDiseaseType]
 */
class ComputationUseCaseImpl (private val compRepo: ComputationRepository): ComputationUseCase {

    override fun changeEvalParameter(
        item: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) = compRepo.changeEvalParameter(item, inputDoubleValue, inputBooleanValue)

    override fun convertEvalValue(item: AbstractDiseaseType): String = compRepo.convertEvalValue(item)
}
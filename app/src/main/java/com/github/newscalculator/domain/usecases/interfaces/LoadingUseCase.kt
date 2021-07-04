package com.github.newscalculator.domain.usecases.interfaces

import com.github.newscalculator.domain.entities.AbstractDiseaseType

interface LoadingUseCase {
    suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>, message: String?) -> Unit,
        onFailLoad: (MutableList<AbstractDiseaseType>?, String?) -> Unit
    )
}
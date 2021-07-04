package com.github.newscalculator.domain.usecases

import com.github.newscalculator.data.loadingData.LoadParametersRepository
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.interfaces.LoadingUseCase

class LoadingUseCaseImpl (private val loadingRepo: LoadParametersRepository): LoadingUseCase {
    override suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>, message: String?) -> Unit,
        onFailLoad: (MutableList<AbstractDiseaseType>?, String?) -> Unit
    ) {
        loadingRepo.loadParameters(onLoadParameters, onFailLoad)
    }
}
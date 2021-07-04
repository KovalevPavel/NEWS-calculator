package com.github.newscalculator.data.loadingData.local

import com.github.newscalculator.data.loadingData.LoadParametersRepository
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadParametersRepositoryLocal: LoadParametersRepository {
    override suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>, String?) -> Unit,
        onFailLoad: (MutableList<AbstractDiseaseType>?, String?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            onLoadParameters (mutableListOf(), "test")
        }
    }
}
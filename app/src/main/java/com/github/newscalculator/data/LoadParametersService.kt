package com.github.newscalculator.data

import com.github.newscalculator.domain.entities.AbstractDiseaseType

/**
 * Загрузчик списка измеряемых параметров.
 */
interface LoadParametersService {
    /**
     * Метод загрузки списка измеряемых параметров из источника.
     *
     * @return Список [MutableList] измеряемых параметров [AbstractDiseaseType]
     */
    suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>) -> Unit,
        onFailLoad: (critical: Boolean, itemList: MutableList<AbstractDiseaseType>?) -> Unit
    )
}
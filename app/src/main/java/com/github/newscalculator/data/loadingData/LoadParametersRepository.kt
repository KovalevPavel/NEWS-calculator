package com.github.newscalculator.data.loadingData

import com.github.newscalculator.domain.entities.AbstractDiseaseType

/**
 * Загрузчик списка измеряемых параметров.
 */
interface LoadParametersRepository {
    /**
     * Получение списка параметров
     * @param onLoadParameters колбэк, который вызывается после завершения загрузки
     * @param onFailLoad колбэк, который вызывается при невозможности выполнить загрузку
     */
    suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>, String?) -> Unit,
        onFailLoad: (MutableList<AbstractDiseaseType>?, String?) -> Unit
    )
}
package com.github.newscalculator.moshi

/**
 * Типы объектов [com.github.newscalculator.domain.entities.AbstractDiseaseType]
 * Необходим для парсинга приходящего JSON
 * @property NUMERICAL Тип имеет текстовое поле для заполнения
 * @property CHECKABLE Тип имеет булево поле для заполнения
 * @property COMBINED Тип имеет оба поля для заполнения
 */
enum class EvalTypes {
    NUMERICAL,
    CHECKABLE,
    COMBINED
}
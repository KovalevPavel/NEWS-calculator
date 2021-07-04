package com.github.newscalculator.data

import com.github.newscalculator.domain.entities.AbstractDiseaseType
import javax.inject.Inject

class ComputationRepository @Inject constructor() {
    /**
     * Изменение объекта
     * @param item Начальный объект для изменения
     * @param inputDoubleValue Измеренное числовое значение (температура, давление и т.д.)
     * @param inputBooleanValue Измеренное логическое значение (инсуфляция, изменение сознания)
     */
    fun changeEvalParameter(
        item: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) {
        item.apply {
            setMeasuredParameter(inputDoubleValue)
            setBooleanParameter(inputBooleanValue)
            evaluatePoints()
            isModified = true
        }
    }

    /**
     * Метод форматирования замеренного числового значения параметра.
     *
     * Если параметр не предполагает использование дробного числа (давление, частота дыхания и т.д.),
     * то перед возвращением приводим измеренное значение к типу [Int], отсекая тем самым дробную часть.
     * @param item Объект, измеренное значение которого необходимо форматировать
     * @return Строка, содержащая измеренное значение
     */
    fun convertEvalValue(item: AbstractDiseaseType): String {
        val numberParameter = item.getNumberParameter
        return when (item.fractional) {
            true -> if (numberParameter == -1.0) "" else "$numberParameter"
            false -> if (numberParameter == -1.0) "" else "${numberParameter.toInt()}"
        }
    }
}
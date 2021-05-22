package com.github.newscalculator.ui.editvalueDialog

import com.github.newscalculator.domain.entities.AbstractDiseaseType

class EditValueRepository {
    /**
     * Метод форматирования замеренного числового значения параметра.
     *
     * Если параметр не предполагает использование дробного числа (давление, частота дыхания и т.д.),
     * то перед возвращением приводим измеренное значение к типу [Int], отсекая тем самым дробную часть.
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
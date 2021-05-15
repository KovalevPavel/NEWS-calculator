package com.github.newscalculator.diseaseparameterstypes

import android.os.Parcelable
import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.Json

/*
Родительский класс типов измеряемых параметров

    normalValue - нормальное значение измеряемого параметра. Используется в editText.hint
    fractional - указывает, будет ли учитываться, что в поле ввода параметра будет дробь
    required - обязательный параметр или нет.
            Если нет - итоговый расчет будет проводиться независимо от того, заполнялся он или нет
    arrayOfEvalLevels - границы диапозонов измеряемых значений
    arrayOfDiseasePoints - оценки, соответствующие диапазонам значений
    measuredArray - массив с измеренными значениями
    resultPointsArray - массив с полученными оценками
    shortString - переменная для идентификации добавок. Используется в Checkable и Combined
 */

abstract class AbstractDiseaseType(
    @Json(name = "type") val type: EvalTypes,
) : Parcelable {
    companion object {
        /*
        размер массива под измеряемые параметры
        Зарезервированные индексы:
        0 - числовые параметры (температура, давление и т.д.)
        1 - булевы параметры (необходимость инсуфляции, изменение сознания)
         */
        private const val ARRAY_SIZE = 2
    }

    abstract val id: Long
    abstract val parameterName: String
    abstract val normalValue: Double
    abstract val fractional: Boolean
    abstract val required: Boolean
    open val arrayOfEvalLevels: Array<Double> = emptyArray()
    open val arrayOfDiseasePoints: Array<Int> = emptyArray()
    val measuredArray: Array<Any?> = arrayOfNulls(ARRAY_SIZE)
    open val shortString = ""
    var isModified = false

    val resultPointsArray: Array<Int> = Array(ARRAY_SIZE) {
        0
    }

    val getMeasuredPoints: Int
        get() = resultPointsArray[0]
    val getBooleanPoints: Int
        get() = resultPointsArray[1]
    val getNumberParameter: Double
        get() = measuredArray[0] as? Double ?: -1.0
    val getBooleanParameter: Boolean
        get() = measuredArray[1] as? Boolean ?: false

    fun setMeasuredParameter(measuredParameter: Double) {
        measuredArray[0] = measuredParameter
    }

    fun setBooleanParameter(parameter: Boolean) {
        measuredArray[1] = parameter
    }

    fun setMeasuredPoints(points: Int) {
        resultPointsArray[0] = points
    }

    fun setBooleanPoints(points: Int) {
        resultPointsArray[1] = points
    }

    fun getResultPoints() = resultPointsArray.sum()

    abstract fun evaluatePoints()

    //функция получения строки с замеренными значениями
    abstract fun createMeasuredString(): String

    //функция получения строки с баллами для отображения
    abstract fun createPointsString(): String
}
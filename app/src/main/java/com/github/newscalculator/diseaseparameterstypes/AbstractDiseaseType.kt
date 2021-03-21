package com.github.newscalculator.diseaseparameterstypes

import android.os.Parcelable
import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.Json

abstract class AbstractDiseaseType(@Json(name = "type") val type: EvalTypes) : Parcelable {
    companion object {
        private const val ARRAY_SIZE = 2
    }

    abstract val id: Long

    //нормальное значение измеряемого параметра. Используется в editText.hint
    abstract val normalValue: Double

    abstract val parameterName: String

    //границы диапозонов измеряемых значений
    open val arrayOfEvalLevels: Array<Double> = emptyArray()

    //оценки, соответствующие диапазонам значений
    open val arrayOfDiseasePoints: Array<Int> = emptyArray()

    //флаг изменения параметра в текущей сессии
    var isModified = false

    //переменная для идентификации добавок. Используется в Checkable и Combined
    open val shortString = ""

    //массив с измеренными значениями
    open val measuredArray: Array<Any> = Array(ARRAY_SIZE) {
        0.0
    }

    //массив с оценками
    open val resultPointsArray: Array<Int> = Array(ARRAY_SIZE) {
        0
    }

    //расчет результирующей оценки
    open fun evaluatePoints() {
        val minIndex =
            arrayOfEvalLevels.indexOfFirst {
                arrayOfEvalLevels.indexOf(it) % 2 == 1 &&
                        (measuredArray[0] as Int) <= it
            }

        val numericalPoints =
            if (minIndex > 0) arrayOfDiseasePoints[(minIndex - 1) / 2]
            else arrayOfDiseasePoints[arrayOfDiseasePoints.lastIndex]

        resultPointsArray[0] = numericalPoints
    }

    //общая сумма оценок
    fun getResultPoints() = resultPointsArray.sum()

    //функция получения строки с баллами для отображения
    open fun createPointsString(): String = "${resultPointsArray[0]}"
}
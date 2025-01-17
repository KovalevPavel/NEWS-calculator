package com.github.newscalculator.domain.entities

import android.os.Parcelable
import com.github.newscalculator.domain.entities.AbstractDiseaseType.Companion.ARRAY_SIZE

/**
 * Родительский класс типов измеряемых параметров
 * @property normalValue Нормальное значение измеряемого параметра. Используется в EditText.hint
 * @property fractional Указывает, будет ли учитываться, что в поле ввода параметра будет дробь
 * @property required Обязательный параметр или нет.
 * Если указано false - итоговый расчет будет проводиться независимо от того, заполнялся параметр или нет
 * @property arrayOfEvalLevels Границы диапозонов измеряемых значений
 * @property arrayOfDiseasePoints Оценки, соответствующие диапазонам значений
 * @property measuredArray Массив с измеренными значениями. Не делать приватным, хоть студия и рекомендует обратное!
 * @property resultPointsArray Массив с полученными оценками. Не делать приватным, хоть студия и рекомендует обратное!
 * @property shortString Переменная для идентификации добавок. Используется в [CheckableDiseaseType] и [CombinedDiseaseType]
 * @property isModified Флаг измененного состояния
 * @property ARRAY_SIZE Размер массива под изменяемые параметры.
 * Зарезеовированные индексы:
 * - 0 - числовые параметры (температура, давление и т.д.)
 * - 1 - булевы параметры (необходимость инсуфляции, изменение сознания)
 */
abstract class AbstractDiseaseType(
    val type: EvalTypes,
) : Parcelable {
    companion object {
        fun <T: Number> ArrayList<T>.convertToDoubleArray(): Array<Double> {
            return Array(this.size) {
                this[it].toDouble()
            }
        }

        fun <T: Number> ArrayList<T>.convertToIntArray(): Array<Int> {
            return Array(this.size) {
                this[it].toInt()
            }
        }

        private const val ARRAY_SIZE = 2
        const val ID_ = "id"
        const val PARAM_NAME_ = "parameterName"
        const val TYPE_ = "type"
        const val NORM_VALUE_ = "normalValue"
        const val FRACTIONAL_ = "fractional"
        const val REQUIRED_ = "required"
        const val MAX_CHECK_VAL_ = "maxCheckableValue"
        const val LABEL_ = "label"
        const val SHORT_STRING_ = "shortString"
        const val ARRAY_LEVELS_ = "arrayOfEvalLevels"
        const val ARRAY_POINTS_ = "arrayOfDiseasePoints"
        const val THRESHOLD_ = "threshold"
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

    /**
     * Сумма ВСЕХ оценок в элементе
     */
    fun getResultPoints() = resultPointsArray.sum()

    /**
     * Вычисление оценок в элементе с занесением в массив результатов
     */
    abstract fun evaluatePoints()

    /**
     * Получения строки с замеренными значениями
     */
    abstract fun createMeasuredString(): String

    /**
     * Получение строки с баллами для отображения
     */
    abstract fun createPointsString(): String

    /**
     * Сброс значений до начальных значений (вызывается при командах очистки)
     */
    abstract fun restoreDefault()
}
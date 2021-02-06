package com.github.newscalculator

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class EvalParameter(
    val id: Int,
    @Json(name = "name") val parameterName: String,
    val normalValue: Double,
    //границы диапазонов значений
    @Json(name = "arrayOFLevels") val arrayOfEvalLevels: Array<Double>,
    //оценки, соответствующие диапазонам
    @Json(name = "arrayOfMarks") val arrayOfDiseasePoints: Array<Int>,
    //добавка чек-бокса (если есть)
    val maxCheckableValue: Int = 0,
    @Json(name = "spMark") val specialMark: String = "",
    //измеренное значение
    var measuredValue: Double?,
    //получившаяся оценка
    var diseasePoints: Int = 0,
    //дополнительная оценка по чек-боксу
    var diseaseBooleanPoints: Int = 0,
    //изменялся в текущей сессии или нет
    var isModified: Boolean = false
) : Parcelable
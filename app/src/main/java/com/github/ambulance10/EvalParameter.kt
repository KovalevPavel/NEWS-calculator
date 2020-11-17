package com.github.ambulance10

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EvalParameter(
    val parameterName: String,
    val normalValue: Double,
    val arrayOfEvalLevels: Array<Double>,
    val arrayOfDiseasePoints: Array<Int>,
    val maxCheckableValue: Int = 0,
    val specialMark: String? = null,
    var evalValue: Double,
    var diseasePoints: Int,
    var diseaseBooleanPoints: Int
): Parcelable
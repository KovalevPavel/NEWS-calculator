package com.github.newscalculator.screens.mainfragment

import android.content.res.Resources
import com.github.newscalculator.EvalParameter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class Mainrepository {

    companion object {
        private const val OXYGEN_INSUFFLATION = 1
        private const val CONSCIOUS_CHANGE = 3
    }

    private val moshi = Moshi.Builder()
        .build()
    private val evalListType =
        Types.newParameterizedType(List::class.java, EvalParameter::class.java)
    private val moshiAdapter = moshi
        .adapter<MutableList<EvalParameter>>(evalListType).nonNull()

    fun changeEvalParameter(
        item: EvalParameter,
        inputDoubleValue: Double?,
        inputBooleanValue: Boolean
    ): EvalParameter {
        item.apply {
            measuredValue = inputDoubleValue
            calculateLevel(inputDoubleValue)
            calculateBooleanLevel(inputBooleanValue)
            isModified = true
        }
        return item
    }

    private fun EvalParameter.calculateLevel(inputParameter: Double?) {
        val measuredValue = inputParameter?.let {
            if (id != 2) it.toInt().toDouble() else inputParameter
        }
        diseasePoints = if (measuredValue == null) 0
        else {
            val minIndex =
                arrayOfEvalLevels.indexOfFirst {
                    arrayOfEvalLevels.indexOf(it) % 2 == 1 &&
                            measuredValue <= it
                }
            if (minIndex > 0) arrayOfDiseasePoints[(minIndex - 1) / 2] else arrayOfDiseasePoints[arrayOfDiseasePoints.lastIndex]
        }
    }

    private fun EvalParameter.calculateBooleanLevel(inputValue: Boolean) {
        diseaseBooleanPoints = when (specialMark) {
            "Инсуфляция кислорода" -> if (inputValue) OXYGEN_INSUFFLATION else 0
            "Изменение уровня сознания" -> if (inputValue) CONSCIOUS_CHANGE else 0
            else -> 0
        }
    }


    private fun readDataFromJSON(res: Resources): JSONArray {
        return try {
            JSONArray(
                res.assets.open("evalItemsList.json").bufferedReader()
                    .readText()
            )
        } catch (e: JSONException) {
            JSONArray("[]")
        }
    }

    suspend fun generateEval(res: Resources): MutableList<EvalParameter> {
        return withContext(Dispatchers.IO) {
            val incomingJSONArray = readDataFromJSON(res).toString()
            moshiAdapter.fromJson(incomingJSONArray) ?: mutableListOf()
        }
    }
}
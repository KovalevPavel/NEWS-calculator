package com.github.ambulance10

import android.content.res.Resources
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EvalRepository {

    companion object {
        private const val NAME = "name"
        private const val NORM_VALUE = "normalValue"
        private const val ARRAY_LEVELS = "arrayOFLevels"
        private const val ARRAY_OF_MARKS = "arrayOfMarks"
        private const val MAX_CHECKABLE_MARK = "maxCheckableValue"
        private const val SPECIAL_MARK = "spMark"
        private const val OXYGEN_INSUFFLATION = 1
        private const val CONSCIOUS_CHANGE = 3
    }

    fun calculateLevel(item: EvalParameter, inputParameter: Double): Int {
        return if (inputParameter == (-1).toDouble()) 0
        else {
            val minIndex =
                item.arrayOfEvalLevels.indexOfFirst {
                    item.arrayOfEvalLevels.indexOf(it) % 2 == 1 &&
                            inputParameter <= it
                }
            if (minIndex > 0) item.arrayOfDiseasePoints[(minIndex - 1) / 2] else item.arrayOfDiseasePoints[item.arrayOfDiseasePoints.lastIndex]
        }
    }

    fun calculateBooleanLevel(item: EvalParameter, inputValue: Boolean) =
        when (item.specialMark) {
            "Инсуфляция кислорода" -> if (inputValue) OXYGEN_INSUFFLATION else 0
            "Изменение уровня сознания" -> if (inputValue) CONSCIOUS_CHANGE else 0
            else -> 0
        }

    fun generateEmptyDiseasePointsList(inputEvalParametersList: MutableList<EvalParameter>): List<Int> =
        List(inputEvalParametersList.size) { 0 }

    fun generateBooleanList(inputEvalParametersList: MutableList<EvalParameter>): List<Boolean> =
        MutableList(inputEvalParametersList.size) { false }

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

    fun generateEval(res: Resources): MutableList<EvalParameter> {
        val incomingJSONArray = readDataFromJSON(res)
        val tempList = mutableListOf<EvalParameter>()
        for (index in 0 until incomingJSONArray.length())
            tempList.add(convertJSONToEval(incomingJSONArray[index] as JSONObject))
        return tempList
    }

    private fun convertJSONToEval(obj: JSONObject) = EvalParameter(
        parameterName = obj.getString(NAME),
        normalValue = obj.getDouble(NORM_VALUE),
        arrayOfEvalLevels = getDoubleArrayFromJSON(obj.getJSONArray(ARRAY_LEVELS)),
        arrayOfDiseasePoints = getIntArrayFromJSON(obj.getJSONArray(ARRAY_OF_MARKS)),
        maxCheckableValue = obj.getInt(MAX_CHECKABLE_MARK),
        specialMark = try {
            obj.getString(SPECIAL_MARK)
        } catch (e: JSONException) {
            null
        },
        evalValue = 0.0,
        diseasePoints = 0,
        diseaseBooleanPoints = 0
    )

    private fun getDoubleArrayFromJSON(obj: JSONArray): Array<Double> {
        val tempList = mutableListOf<Double>()
        for (i in 0 until obj.length())
            tempList.add(obj.getDouble(i))
        return tempList.toTypedArray()
    }

    private fun getIntArrayFromJSON(obj: JSONArray): Array<Int> {
        val tempList = mutableListOf<Int>()
        for (i in 0 until obj.length())
            tempList.add(obj.getInt(i))
        return tempList.toTypedArray()
    }
}
package com.github.newscalculator.screens.mainfragment

import android.content.res.Resources
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CheckableDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CombinedDiseaseType
import com.github.newscalculator.diseaseparameterstypes.NumericalDiseaseType
import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class MainRepository {

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(AbstractDiseaseType::class.java, "type")
                .withSubtype(NumericalDiseaseType::class.java, EvalTypes.Numerical.name)
                .withSubtype(CheckableDiseaseType::class.java, EvalTypes.Checkable.name)
                .withSubtype(CombinedDiseaseType::class.java, EvalTypes.Combined.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    private val myType =
        Types.newParameterizedType(MutableList::class.java, AbstractDiseaseType::class.java)

    val moshiAdapter = moshi.adapter<MutableList<AbstractDiseaseType>>(myType).nonNull()

    fun changeEvalParameter(
        item: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) {
        item.apply {
            measuredArray[0] = inputDoubleValue
            measuredArray[1] = inputBooleanValue
            evaluatePoints()
            isModified = true
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

    suspend fun generateEval(res: Resources): MutableList<AbstractDiseaseType> {
        return withContext(Dispatchers.IO) {
            val incomingJSONArray = readDataFromJSON(res).toString()
            moshiAdapter.fromJson(incomingJSONArray) ?: mutableListOf()
        }
    }
}
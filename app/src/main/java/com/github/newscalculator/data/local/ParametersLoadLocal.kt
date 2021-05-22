package com.github.newscalculator.data.local

import android.content.Context
import android.content.res.Resources
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.data.local.ParametersLoadLocal.Companion.LABEL_KEY
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.entities.CheckableDiseaseType
import com.github.newscalculator.domain.entities.CombinedDiseaseType
import com.github.newscalculator.domain.entities.NumericalDiseaseType
import com.github.newscalculator.moshi.EvalTypes
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

/**
 * Загрузка списка измеряемых параметров из локального хранилища.
 *
 * @property moshi Объект [Moshi] с подключенным полиморфным адаптером. В качестве ключевого поля выступает [LABEL_KEY].
 * @property myType Дает понять [moshi], что необходимо будет парсить список [MutableList], в котором содержатся объекты [AbstractDiseaseType]
 * @see <a href="Руководство по полиморфным адаптерам">https://proandroiddev.com/moshi-polymorphic-adapter-is-d25deebbd7c5</a>
 */

class ParametersLoadLocal(private val applicationContext: Context) : LoadParametersService {
    companion object {
        private const val LABEL_KEY = "type"
    }

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(AbstractDiseaseType::class.java, LABEL_KEY)
                .withSubtype(NumericalDiseaseType::class.java, EvalTypes.NUMERICAL.name)
                .withSubtype(CheckableDiseaseType::class.java, EvalTypes.CHECKABLE.name)
                .withSubtype(CombinedDiseaseType::class.java, EvalTypes.COMBINED.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    private val myType =
        Types.newParameterizedType(MutableList::class.java, AbstractDiseaseType::class.java)

    private val moshiAdapter = moshi.adapter<MutableList<AbstractDiseaseType>>(myType).nonNull()

    /**
     * Чтение данных из Json.
     */
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

    override suspend fun loadParameters(): MutableList<AbstractDiseaseType> {
        return withContext(Dispatchers.IO) {
            val incomingJSONArray = readDataFromJSON(applicationContext.resources).toString()
            moshiAdapter.fromJson(incomingJSONArray) ?: mutableListOf()
        }
    }
}
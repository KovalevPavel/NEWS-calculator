package com.github.newscalculator.domain.usecases

import android.content.Context
import android.widget.Toast
import com.github.newscalculator.domain.entities.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

/**
 * Класс с операциями чтения/записи списка измеряемых параметров
 */
class HandleLocalParametersListUseCase(private val context: Context) {
    companion object {
        private const val LABEL_KEY = "type"
        private const val FILE_NAME = "eval_item_list.json"
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

    private val fileOperationsCoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Toast.makeText(
                context,
                "Ошибка при работе с файловой системой:\n$throwable",
                Toast.LENGTH_LONG
            ).show()
        }

    /**
     * Запись нового списка измеряемых параметров в файл локального хранилища
     * @param parametersList Новый список параметров
     */
    suspend fun writeNewParameters(parametersList: MutableList<AbstractDiseaseType>) {
        withContext(Dispatchers.IO + fileOperationsCoroutineExceptionHandler) {
            val file = File(context.filesDir, FILE_NAME)
            file.outputStream().use {
                it.write(moshiAdapter.toJson(parametersList).toByteArray())
            }
        }
    }

    /**
     * Чтение списка измеряемых параметров из локального хранилища
     * @return Список измеряемых параметров
     */
    suspend fun getParametersList(): MutableList<AbstractDiseaseType> {
        return withContext(Dispatchers.IO + fileOperationsCoroutineExceptionHandler) {
            val incomingJSONArray = readDataFromJSON().toString()
            moshiAdapter.fromJson(incomingJSONArray) ?: mutableListOf()
        }
    }

    //    Чтение данных из Json.
    private fun readDataFromJSON(): JSONArray {
        return JSONArray(
            File(context.filesDir, FILE_NAME).bufferedReader().use {
                it.readText()
            }
        )
    }
}
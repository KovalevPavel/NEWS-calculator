package com.github.newscalculator.data.remote

import com.github.newscalculator.MyApplication
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.data.remote.ParametersLoadRemote.Companion.REFRESH_DELAY
import com.github.newscalculator.domain.entities.*
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Класс отвечает за загрузку данных с сервера.
 *
 * Обращение к серверу FireStore происходит при первом запуске и далее через каждые 12 часов при запуске приложения.
 * В случае, если версия базы данных на сервере отличается от версии, записанной в SharedPreferences, происходит обновление базы на
 * устройстве. В противном случае список загружается из локального хранилища.
 *
 * @property REFRESH_DELAY Минимальный временной промежуток между обновлениями, мс
 */
class ParametersLoadRemote(
    private val db: FirebaseFirestore,
    private val sPrefsUseCase: SharedPrefsUseCase
) : LoadParametersService {
    companion object {
        private const val REFRESH_DELAY = 43200000L
    }

    private var resultList: MutableList<AbstractDiseaseType>? = null

    /**
     * Получение списка параметров
     * @param onLoadParameters колбэк, который вызывается после завершения загрузки
     */
    override suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>) -> Unit,
        onFailLoad: (critical: Boolean, itemList: MutableList<AbstractDiseaseType>?) -> Unit
    ) {
//        пытаемся получить данные из локального хранилища
        getRemoteData(
            Source.CACHE,
            onFail = {
                CoroutineScope(Dispatchers.IO).launch {
                    getRemoteData(Source.SERVER, { remoteData ->
                        val remoteItems = handleDataFromDatabase(remoteData)
                        onLoadParameters(remoteItems)
                    }, {
                        onFailLoad(true, null)
                    })
                }
            },
            onGetData = { itemsList ->
                CoroutineScope(Dispatchers.IO).launch {
                    val lastUpdateTime = sPrefsUseCase.getLastLaunchTime()
                    if (System.currentTimeMillis() - lastUpdateTime > REFRESH_DELAY) {
//                        требуется синхронизация
                        getRemoteData(Source.SERVER, { remoteData ->
                            CoroutineScope(Dispatchers.IO).launch {
                                sPrefsUseCase.resetUpdateTime()
                                val remoteItems = handleDataFromDatabase(remoteData)
                                onLoadParameters(remoteItems)
                            }
                        }, {
                            onFailLoad(false, handleDataFromDatabase(itemsList))
                        })
                    } else {
                        onLoadParameters(handleDataFromDatabase(itemsList))
                    }
                }
            }
        )
/*
        resultList?.let { itemsList ->
            val lastUpdateTime = sPrefsUseCase.getLastLaunchTime()
            if (System.currentTimeMillis() - lastUpdateTime > REFRESH_DELAY) {
//                требуется синхронизация
                getRemoteData(Source.SERVER, { remoteData ->
                    CoroutineScope(Dispatchers.IO).launch {
                        sPrefsUseCase.resetUpdateTime()
                        val remoteItems = handleDataFromDatabase(remoteData)
                        onLoadParameters(remoteItems)
                    }
                }, {
                    onFailLoad(false, itemsList)
                })
            } else {
                onLoadParameters(itemsList)
            }
        } ?: run {
            getRemoteData(Source.SERVER, { remoteData ->
                CoroutineScope(Dispatchers.IO).launch {
                    val remoteItems = handleDataFromDatabase(remoteData)
                    onLoadParameters(remoteItems)
                }
            }, {
                onFailLoad(true, null)
            })
            return
        }

 */
    }

    private fun handleDataFromDatabase(remoteData: MutableList<DocumentSnapshot>): MutableList<AbstractDiseaseType> {
        val remoteItems = remoteData
            .mapNotNull {
                it.data?.toItemType()
            }.toMutableList()
        remoteItems.sortBy {
            it.id
        }
        return remoteItems
    }

    private suspend fun getRemoteData(
        source: Source,
        onGetData: (MutableList<DocumentSnapshot>) -> Unit,
        onFail: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val docRef =
                db.collection(MyApplication.COLLECTION_NAME)
            docRef.get(source)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val data = task.result
                        data?.let {
                            if (data.size() == 0) onFail()
                            else onGetData(data.documents)
                        } ?: onFail()
                    } else {
                        onFail()
                    }
                }
        }
    }

    private fun MutableMap<String, Any>.toItemType(): AbstractDiseaseType {
        return when (
            EvalTypes.valueOf(this["type"] as String)
        ) {
            EvalTypes.CHECKABLE -> CheckableDiseaseType.convertFromMap(this)
            EvalTypes.NUMERICAL -> NumericalDiseaseType.convertFromMap(this)
            EvalTypes.COMBINED -> CombinedDiseaseType.convertFromMap(this)
        }
    }
}
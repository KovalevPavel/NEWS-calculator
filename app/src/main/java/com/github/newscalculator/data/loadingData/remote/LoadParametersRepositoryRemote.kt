package com.github.newscalculator.data.loadingData.remote

import android.app.Application
import com.github.newscalculator.R
import com.github.newscalculator.app.MyApplication
import com.github.newscalculator.data.loadingData.LoadParametersRepository
import com.github.newscalculator.data.loadingData.remote.LoadParametersRepositoryRemote.Companion.REFRESH_DELAY
import com.github.newscalculator.data.sharePrefs.SharePrefsRepository
import com.github.newscalculator.domain.entities.*
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
class LoadParametersRepositoryRemote(
    private val db: FirebaseFirestore,
    private val sPrefsRepo: SharePrefsRepository,
    private val context: Application
) : LoadParametersRepository {
    companion object {
        private const val REFRESH_DELAY = 43200000L
    }

    override suspend fun loadParameters(
        onLoadParameters: (MutableList<AbstractDiseaseType>, message: String?) -> Unit,
        onFailLoad: (MutableList<AbstractDiseaseType>?, String?) -> Unit
    ) {
//        пытаемся получить данные из локального хранилища
        getRemoteData(
            Source.CACHE,
            onFail = {
                CoroutineScope(Dispatchers.IO).launch {
                    getRemoteData(Source.SERVER, { remoteData ->
                        val remoteItems = handleDataFromDatabase(remoteData)
                        onLoadParameters(remoteItems, null)
                    }, {
                        onFailLoad(null, null)
                    })
                }
            },
            onGetData = { itemsList ->
                CoroutineScope(Dispatchers.IO).launch {
                    val lastUpdateTime = sPrefsRepo.getLastLaunchTime()
                    if (System.currentTimeMillis() - lastUpdateTime > REFRESH_DELAY) {
//                        требуется синхронизация
                        getRemoteData(Source.SERVER, { remoteData ->
                            CoroutineScope(Dispatchers.IO).launch {
                                sPrefsRepo.resetUpdateTime()
                                val remoteItems = handleDataFromDatabase(remoteData)
                                val string = context.getString(R.string.synchronized_successful)
                                onLoadParameters(remoteItems, string)
                            }
                        }, {
                            val string = context.getString(R.string.lastLocalLoaded)
                            onFailLoad(handleDataFromDatabase(itemsList), string)
                        })
                    } else {
                        onLoadParameters(handleDataFromDatabase(itemsList), null)
                    }
                }
            }
        )
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
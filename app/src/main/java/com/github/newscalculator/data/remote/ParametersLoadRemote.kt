package com.github.newscalculator.data.remote

import com.github.newscalculator.MyApplication
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.entities.CheckableDiseaseType
import com.github.newscalculator.domain.entities.CombinedDiseaseType
import com.github.newscalculator.domain.entities.NumericalDiseaseType
import com.github.newscalculator.domain.usecases.HandleLocalParametersListUseCase
import com.github.newscalculator.domain.usecases.SharedPrefsUseCase
import com.github.newscalculator.moshi.EvalTypes
import com.github.newscalculator.util.loggingDebug
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParametersLoadRemote(
    private val db: FirebaseFirestore,
    private val sPrefsUseCase: SharedPrefsUseCase,
    private val localFilesUseCase: HandleLocalParametersListUseCase
) : LoadParametersService {
    companion object {
        private const val REFRESH_DELAY = 43200000L
    }

    override suspend fun loadParameters(onLoadParameters: (MutableList<AbstractDiseaseType>) -> Unit) {
        val lastLaunchTime = sPrefsUseCase.getLastLaunchTime()
        sPrefsUseCase.updateLaunchTime()
        if (System.currentTimeMillis() - lastLaunchTime > REFRESH_DELAY)
            getRemoteData { remoteData ->
                CoroutineScope(Dispatchers.IO).launch {
                    val remoteVersion = remoteData.find {
                        it.id == MyApplication.DB_VERSION
                    }?.data?.get(MyApplication.DB_VERSION) as Long

                    val remoteItems = remoteData.filter {
                        it.id != MyApplication.DB_VERSION
                    }.mapNotNull {
                        it.data?.toItemType()
                    }.toMutableList()
                    remoteItems.sortBy {
                        it.id
                    }

                    if (compareWithLocalVersion(remoteVersion).not()) {
                        sPrefsUseCase.updateDbVersion(remoteVersion)
                        localFilesUseCase.writeNewParameters(remoteItems)
                    }
                    onLoadParameters(remoteItems)
                }
            }
        else {
            loggingDebug("loading from local on thread ${Thread.currentThread().name}")
            val list = localFilesUseCase.getParametersList()
            onLoadParameters(list)
        }
    }

    private suspend fun compareWithLocalVersion(remoteVersion: Long): Boolean {
        val currentVersion = sPrefsUseCase.getCurrentDbVersion()
        return currentVersion == remoteVersion
    }

    private suspend fun getRemoteData(onGetData: (MutableList<DocumentSnapshot>) -> Unit) {
        withContext(Dispatchers.IO) {
            val docRef =
                db.collection(MyApplication.COLLECTION_NAME)
            docRef.get()
                .addOnSuccessListener { data ->
                    onGetData(data.documents)
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
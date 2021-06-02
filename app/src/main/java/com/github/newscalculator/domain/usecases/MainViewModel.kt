package com.github.newscalculator.domain.usecases

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.newscalculator.R
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Хранит состояние главного экрана с RecyclerView
 * @param loadingService сервис получения списка измеряемых параметров
 * @property itemsList список оцениваемых параметров (строки таблицы NEWS)
 * @property changedItem переменная для измененного параметра. Используется для передачи в RecyclerView
 * @property everythingIsEnteredEvent событие об изменении всех обязательных параметров
 * @property totalSum сумма всех баллов
 */

class MainViewModel(
    private val loadingService: LoadParametersService,
    private val myApplication: Application
) : AndroidViewModel(myApplication) {

    private val itemsList = MutableLiveData<MutableList<AbstractDiseaseType>>()
    private val changedItem = MutableLiveData<AbstractDiseaseType>()
    private val everythingIsEnteredEvent = SingleLiveEvent<Int>()
    private val loadErrorEvent = SingleLiveEvent<Unit>()
    private val toastEvent = SingleLiveEvent<String>()

    val getItemsList: LiveData<MutableList<AbstractDiseaseType>>
        get() = itemsList

    val getChangedItem: LiveData<AbstractDiseaseType>
        get() = changedItem

    val getEverythingIsEntered: LiveData<Int>
        get() = everythingIsEnteredEvent

    val getLoadErrorEvent: LiveData<Unit>
        get() = loadErrorEvent

    val getToastEvent: LiveData<String>
        get() = toastEvent

    private val totalSum: Int
        get() = itemsList.value.orEmpty().sumOf {
            it.getResultPoints()
        }

    /**
     * Начальная загрузка таблицы
     */
    fun getItemsList() {
        if (itemsList.value == null) {
            CoroutineScope(Dispatchers.IO).launch {
                loadingService.loadParameters({
                    itemsList.postValue(it)
                }, { critical, list ->
                    val string =
                        if (critical) myApplication.getString(R.string.checkInternet) else
                            myApplication.getString(R.string.lastLocalLoaded)
                    if (critical)
                        loadErrorEvent.postValue(Unit)
                    list?.let {
                        itemsList.postValue(it)
                    }
                    toastEvent.postValue(myApplication.getString(R.string.notSynchronized)+". " + string)
                })
            }
        }
    }

    fun resetItem(item: AbstractDiseaseType) {
        item.restoreDefault()
        changedItem.postValue(item)
    }

    suspend fun refreshList(oldList: MutableList<AbstractDiseaseType>) {
        oldList.filter {
            it.isModified
        }.forEach {
            it.restoreDefault()
            changedItem.postValue(it)
            delay(100)
        }
    }

    /**
     * Изменение текущего параметра и оповещение подписчиков.
     * @param itemToChange Входной параметр для изменения
     * @param inputDoubleValue Измеренное числовое значение (температура, давление и т.д.)
     * @param inputBooleanValue Измеренное логическое значение (инсуфляция, изменение сознания)
     * @see DiseaseTypeUseCase
     */
    fun changeInputValue(
        itemToChange: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) {
        DiseaseTypeUseCase().changeEvalParameter(
            itemToChange,
            inputDoubleValue,
            inputBooleanValue
        )
        changedItem.postValue(itemToChange)
    }

    /**
     * Проверка внесения изменений во все обязательные поля и оповещение подписчиков
     */
    fun checkEverythingIsEntered() {
        itemsList.value.orEmpty().filter { currentItem ->
            currentItem.required
        }.forEach { currentItem ->
            if (currentItem.isModified.not()) return
        }
        everythingIsEnteredEvent.postValue(totalSum)
    }
}
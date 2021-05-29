package com.github.newscalculator.ui.mainFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.DiseaseTypeUseCase
import com.github.newscalculator.util.SingleLiveEvent
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

class MainViewModel(private val loadingService: LoadParametersService) : ViewModel() {

    private val itemsList = MutableLiveData<MutableList<AbstractDiseaseType>>()
    val getItemsList: LiveData<MutableList<AbstractDiseaseType>>
        get() = itemsList

    private val changedItem = MutableLiveData<AbstractDiseaseType>()
    val getChangedItem: LiveData<AbstractDiseaseType>
        get() = changedItem

    private val everythingIsEnteredEvent = SingleLiveEvent<Int>()
    val getEverythingIsEntered: LiveData<Int>
        get() = everythingIsEnteredEvent

    private val totalSum: Int
        get() = itemsList.value.orEmpty().sumBy {
            it.getResultPoints()
        }

    /**
     * Начальная загрузка таблицы
     */
    fun getItemsList() {
        if (itemsList.value == null)
            viewModelScope.launch(Dispatchers.IO) {
                loadingService.loadParameters {
                    itemsList.postValue(it)
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
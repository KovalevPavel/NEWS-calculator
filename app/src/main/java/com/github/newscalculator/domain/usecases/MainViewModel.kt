package com.github.newscalculator.domain.usecases

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.MyApplication
import com.github.newscalculator.R
import com.github.newscalculator.data.LoadParametersService
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.util.SingleLiveEvent
import com.github.newscalculator.util.loggingDebug
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
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    private val itemsList = MutableLiveData<MutableList<AbstractDiseaseType>>()
    private val changedItem = MutableLiveData<AbstractDiseaseType>()
    private val everythingIsEnteredEvent = SingleLiveEvent<Int>()
    private val loadErrorEvent = SingleLiveEvent<Unit>()
    private val toastEvent = SingleLiveEvent<String>()
    private val showHelloDialogEvent = SingleLiveEvent<Unit>()

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

    val getShowHelloDialogEvent: LiveData<Unit>
        get() = showHelloDialogEvent

    private val totalSum: Int
        get() = itemsList.value.orEmpty().sumOf {
            it.getResultPoints()
        }

    private val drawerInteraction = MyApplication.appComponent.getNavigationDrawerInteraction()

    /**
     * Проверка на первый запуск
     */
    fun checkFirstLaunch() {
        viewModelScope.launch {
            if (!sharedPrefsUseCase.getHelloDialogShowed())
                showHelloDialogEvent.postValue(Unit)
        }
    }

    /**
     * Начальная загрузка таблицы
     */
    fun getItemsList() {
        if (itemsList.value == null) {
            CoroutineScope(Dispatchers.IO).launch {
                loadingService.loadParameters(onLoadParameters = { list, message ->
                    itemsList.postValue(list)
                    message?.let {
                        toastEvent.postValue(it)
                    }
                }, onFailLoad = { list, message ->
                    list?.let {
                        itemsList.postValue(it)
                    } ?: kotlin.run {
                        loadErrorEvent.postValue(Unit)
                    }
                    message?.let {
                        toastEvent.postValue(it)
                    }
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

    fun handleDrawerLayoutAction(itemId: Int, activity: Activity) {
        drawerInteraction.apply {
            when (itemId) {
                R.id.support_Share -> shareApp(activity)
                R.id.support_Rate -> rateApp(activity)
                R.id.support_Feedback -> reportBug(activity)
                R.id.support_Help -> showHelloDialogEvent.postValue(Unit)
                else -> loggingDebug("unknown id")
            }
        }
    }
}
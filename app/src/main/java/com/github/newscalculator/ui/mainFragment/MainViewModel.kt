package com.github.newscalculator.ui.mainFragment

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.ComputationUseCaseImpl
import com.github.newscalculator.domain.usecases.interfaces.ComputationUseCase
import com.github.newscalculator.domain.usecases.interfaces.LoadingUseCase
import com.github.newscalculator.domain.usecases.interfaces.NavigationDrawerUseCase
import com.github.newscalculator.domain.usecases.interfaces.SharedPrefsUseCase
import com.github.newscalculator.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Хранит состояние главного экрана с RecyclerView
 * @param loadUseCase интерактор, связывающий ViewModel с репозиторием загрузки данных
 * @param compUseCase интерактор, связывающий ViewModel с репозиторием вычисления значений
 * @param sharedPrefsUseCase интерактор, связывающий ViewModel с репозиторием SharedPreferences
 * @property itemsList список оцениваемых параметров (строки таблицы NEWS)
 * @property changedItem переменная для измененного параметра. Используется для передачи в RecyclerView
 * @property everythingIsEnteredEvent событие об изменении всех обязательных параметров
 * @property loadErrorEvent событие показа диалога ошибки загрузки
 * @property toastEvent событие показа тоста
 * @property showHelloDialogEvent событие показа приветственного диалога
 * @property totalSum сумма всех баллов
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val loadUseCase: LoadingUseCase,
    private val compUseCase: ComputationUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase,
    private val navigationDrawerUseCase: NavigationDrawerUseCase
) : ViewModel() {
    companion object {
        private const val HELLO_DIALOG = "hello_dialog"
    }

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

//    private val drawerInteraction = MyApplication.appComponent.getNavigationDrawerInteraction()

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
                loadUseCase.loadParameters(onLoadParameters = { list, message ->
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
     * @see ComputationUseCaseImpl
     */
    fun changeInputValue(
        itemToChange: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) {
        compUseCase.changeEvalParameter(
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
        navigationDrawerUseCase.invoke(itemId, activity)?.let {
            when (it) {
                HELLO_DIALOG -> showHelloDialogEvent.postValue(Unit)
                else -> error("Unexpected NavigationUseCase result -> $it")
            }
        }
    }
}
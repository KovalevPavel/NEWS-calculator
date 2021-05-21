package com.github.newscalculator.ui.mainFragment

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    //список оцениваемых параметров (строки таблцы NEWS)
    private val evalParametersList = MutableLiveData<MutableList<AbstractDiseaseType>>()
    val getEvalParametersList: LiveData<MutableList<AbstractDiseaseType>>
        get() = evalParametersList

    //измененный параметр состояния
    private val changedParameter = MutableLiveData<AbstractDiseaseType>()
    val getChangedParameter: LiveData<AbstractDiseaseType>
        get() = changedParameter

    //событие об изменении всех параметров
    private val everythingIsEnteredLiveData = MutableLiveData<Int>()
    val getEverythingIsEntered: LiveData<Int>
        get() = everythingIsEnteredLiveData

    //начальная загрузка таблицы
    fun getEvalList(res: Resources) {
        viewModelScope.launch {
            val evalList = repository.generateEval(res)
            evalParametersList.postValue(evalList)
        }
    }

    fun changeInputValue(
        parameterToChange: AbstractDiseaseType,
        inputDoubleValue: Double,
        inputBooleanValue: Boolean
    ) {
        repository.changeEvalParameter(
            parameterToChange,
            inputDoubleValue,
            inputBooleanValue
        )
        changedParameter.postValue(parameterToChange)
    }

    fun checkEverythingIsEntered() {
        evalParametersList.value.orEmpty().filter {
            it.required
        }.forEach {
            if (it.isModified.not()) return
        }
        everythingIsEnteredLiveData.postValue(countSum())
    }

    private fun countSum(): Int {
        var sum = 0
        evalParametersList.value.orEmpty().forEach {
            sum += it.getResultPoints()
        }
        return sum
    }
}
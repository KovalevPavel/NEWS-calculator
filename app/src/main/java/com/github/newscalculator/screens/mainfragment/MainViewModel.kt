package com.github.newscalculator.screens.mainfragment

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.EvalParameter
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = Mainrepository()

    //список оцениваемых параметров (строки таблцы NEWS)
    private val evalParametersList = MutableLiveData<MutableList<EvalParameter>>()
    val getEvalParametersList: LiveData<MutableList<EvalParameter>>
        get() = evalParametersList

    //измененный параметр состояния
    private val changedParameter = MutableLiveData<EvalParameter>()
    val getChangedParameter: LiveData<EvalParameter>
        get() = changedParameter

    //массив с текущими измеренными значениями или null (если значение еще не замерено)
    private lateinit var listOfCurrentParameters: MutableList<Int?>

    //событие об изменении всех параметров
    private val everythingIsEnteredLiveData = MutableLiveData<Int>()
    val getEverythingIsEntered: LiveData<Int>
        get() = everythingIsEnteredLiveData

    //список ихмененных параметров для отслеживания изменений
    private var listToMonitor = mutableListOf<EvalParameter>()

    //начальная загрузка таблицы
    fun getEvalList(res: Resources) {
        viewModelScope.launch {
            val evalList = repository.generateEval(res)
            listOfCurrentParameters = MutableList(evalList.size) { null }
            evalParametersList.postValue(evalList)
            listToMonitor = evalList
        }
    }

    fun changeInputValue(
        evalParameter: EvalParameter,
        inputDoubleValue: Double?,
        inputBooleanValue: Boolean
    ) {
        val changedEval =
            repository.changeEvalParameter(evalParameter, inputDoubleValue, inputBooleanValue)

        //высчитываем общую оценку с учетом чекбокса
        val newCommonDiseasePoints = changedEval.diseasePoints + changedEval.diseaseBooleanPoints

        //записываем оценку объекта в список общей картины больного
        listOfCurrentParameters[changedEval.id] =
            if (inputDoubleValue == null || (!inputBooleanValue && inputDoubleValue == -1.0)) null else newCommonDiseasePoints

        //меняем параметр
        changedParameter.postValue(changedEval)

        //заносим в список объектов новый измененный объект
        listToMonitor[changedEval.id] = changedEval
    }

    fun checkEverythingIsChanged() {
        val everythingIsEntered =
            !listToMonitor.map {
                it.isModified
            }.filterIndexed { index, _ -> index != 5 }.contains(false)
        if (everythingIsEntered) {
            val finalSum = countSum()
            everythingIsEnteredLiveData.postValue(finalSum)
        }
    }

    fun countSum(): Int = listOfCurrentParameters.sumOfPoints()

    private fun <T : List<Int?>> T.sumOfPoints(): Int {
        var tempSum = 0
        for (index in indices)
            this[index]?.let {
                if (it > 0) tempSum += it
            }
        return tempSum
    }
}
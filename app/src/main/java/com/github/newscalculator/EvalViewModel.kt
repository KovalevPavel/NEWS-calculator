package com.github.newscalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EvalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EvalRepository()

    //изменяемый объект
    private var editingParameter: MutableLiveData<EvalParameter> = MutableLiveData()
    val getEditingParameter: MutableLiveData<EvalParameter>
        get() = editingParameter

    //список оцениваемых параметров
    private val evalParametersList = repository.generateEval(application.resources)
    val getEvalParametersList: MutableList<EvalParameter>
        get() = evalParametersList

    //массив с текущими измеренными значениями или 0 (если значение еще не замерено)
    private var listOfCurrentParameters =
        List<Int?>(evalParametersList.size) { null } as MutableList

    val getListOfCurrentParameters: List<Int?>
        get() = listOfCurrentParameters

    var isEverythingIsEntered = false

    //суммарная оценка состояния
    private var evalCommonPoints: MutableLiveData<Int> = MutableLiveData(0)
    val getCommonPoints: LiveData<Int>
        get() = evalCommonPoints

    fun changeInputValue(
        evalParameter: EvalParameter,
        inputDoubleValue: Double?,
        inputBooleanValue: Boolean
    ) {
        val position = evalParameter.id

        evalParameter.measuredValue = inputDoubleValue
        evalParameter.diseasePoints =
            repository.calculateLevel(evalParametersList[position], inputDoubleValue)
        evalParameter.diseaseBooleanPoints =
            repository.calculateBooleanLevel(evalParametersList[position], inputBooleanValue)

        var newCommonDiseasePoints = evalParameter.diseasePoints

        if (evalParameter.diseaseBooleanPoints > 0) newCommonDiseasePoints += evalParameter.diseaseBooleanPoints

        listOfCurrentParameters[position] =
            if (inputDoubleValue == null || (!inputBooleanValue && inputDoubleValue == -1.0)) null else newCommonDiseasePoints

        isEverythingIsEntered =
            !listOfCurrentParameters.filterIndexed { index, _ -> index != 5 }.contains(null)

//        Log.d("ZAWARUDO", "$listOfCurrentParameters")

        editingParameter.postValue(evalParameter)
    }

    fun changeSum() {

        val tempSum = listOfCurrentParameters.sumOfPoints()
        evalCommonPoints.postValue(tempSum)
    }

    private fun <T : List<Int?>> T.sumOfPoints(): Int {
        var tempSum = 0
        for (index in indices)
            this[index]?.let {
                if (it > 0) tempSum += it
            }
        return tempSum
    }
}
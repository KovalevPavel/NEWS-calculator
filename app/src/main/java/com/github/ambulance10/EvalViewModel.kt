package com.github.ambulance10

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EvalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EvalRepository()

    //список оцениваемых параметров
    private val evalParametersList = repository.generateEval(application.resources)
    val getEvalParametersList: MutableList<EvalParameter>
        get() = evalParametersList

    //массив с булевыми значениями, отражающими факт изменеия соответствующего значения
    private var diseaseBooleanList: MutableLiveData<List<Boolean>> =
        MutableLiveData(repository.generateBooleanList(evalParametersList))
    val getDiseaseBooleanList: LiveData<List<Boolean>>
        get() = diseaseBooleanList

    //оценка по таблице NEWS
    private var diseasePointsList: MutableLiveData<List<List<Int>>> =
        MutableLiveData(
            mutableListOf(
                repository.generateEmptyDiseasePointsList(evalParametersList),
                repository.generateEmptyDiseasePointsList(evalParametersList)
            )
        )
    val getDiseasePointsList: LiveData<List<List<Int>>>
        get() = diseasePointsList

    //суммарная оценка состояния
    private var evalCommonPoints: MutableLiveData<Int> = MutableLiveData(0)
    val getCommonPoints: LiveData<Int>
        get() = evalCommonPoints

    fun changeInputValue(position: Int, inputDoubleValue: Double, inputBooleanValue: Boolean) {
        val tempDiseasePointsList = diseasePointsList.value.orEmpty().toMutableList()
        val tempBooleanList = diseaseBooleanList.value.orEmpty().toMutableList()

        //обработка числовых значений
        val temp1 = tempDiseasePointsList[0].toMutableList()
        temp1[position] =
            repository.calculateLevel(evalParametersList[position], inputDoubleValue)

        //обработка булевых значений
        val temp2 = tempDiseasePointsList[1].toMutableList()
        temp2[position] =
            repository.calculateBooleanLevel(evalParametersList[position], inputBooleanValue)
        diseasePointsList.postValue(listOf(temp1, temp2))

        //позиция была введена
        tempBooleanList[position] = true
        diseaseBooleanList.postValue(tempBooleanList)
    }

    fun changeSum(inputList: List<List<Int>>) {
        //суммарная оценка NEWS
        val tempSum = inputList[0].sumOfPoints() + inputList[1].sumOfPoints()
        evalCommonPoints.postValue(tempSum)
    }

    private fun <T : List<Int>> T.sumOfPoints(): Int {
        var tempSum = 0
        for (index in indices)
            if (this[index] > 0) tempSum += this[index]
        return tempSum
    }
}
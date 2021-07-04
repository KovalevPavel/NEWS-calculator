package com.github.newscalculator.ui.editvalueDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.interfaces.ComputationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditValueViewModel @Inject constructor(
    private val compUseCase: ComputationUseCase
) : ViewModel() {
    private val inputTextLiveData = MutableLiveData<String>()

    val getInputText: LiveData<String>
        get() = inputTextLiveData

    fun convertValue(item: AbstractDiseaseType) {
        viewModelScope.launch(Dispatchers.IO) {
            val convertedValue = compUseCase.convertEvalValue(item)
            inputTextLiveData.postValue(convertedValue)
        }
    }
}
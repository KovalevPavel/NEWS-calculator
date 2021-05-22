package com.github.newscalculator.ui.mainFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.newscalculator.MyApplication

/**
 * Фабрика получения viewModel
 */
class MyViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MyApplication.appComponent.getMainViewModel() as T
            else -> error("Unknown ViewModel class")
        }
    }
}
package com.github.newscalculator.domain.usecases.interfaces

interface SharedPrefsUseCase {
    suspend fun getHelloDialogShowed(): Boolean
}
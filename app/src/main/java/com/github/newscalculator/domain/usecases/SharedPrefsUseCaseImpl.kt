package com.github.newscalculator.domain.usecases

import com.github.newscalculator.data.sharePrefs.SharePrefsRepository
import com.github.newscalculator.domain.usecases.interfaces.SharedPrefsUseCase

class SharedPrefsUseCaseImpl(
    private val sharePrefsRepository: SharePrefsRepository
) : SharedPrefsUseCase {

    override suspend fun getHelloDialogShowed(): Boolean =
        sharePrefsRepository.getHelloDialogShowed()

}
package com.github.newscalculator.domain.usecases.interfaces

import android.app.Activity

interface NavigationDrawerUseCase {
    operator fun invoke(itemId: Int, activity: Activity): String?
}
package com.github.newscalculator.domain.usecases

import android.app.Activity
import com.github.newscalculator.R
import com.github.newscalculator.data.NavigationDrawerRepository
import com.github.newscalculator.domain.usecases.interfaces.NavigationDrawerUseCase

class NavigationDrawerUseCaseImpl(private val drawerRepo: NavigationDrawerRepository) :
    NavigationDrawerUseCase {
    companion object {
        private const val HELLO_DIALOG = "hello_dialog"
    }

    override fun invoke(itemId: Int, activity: Activity): String? {
        return when (itemId) {
            R.id.support_Share -> {
                drawerRepo.shareApp(activity)
                null
            }
            R.id.support_Rate -> {
                drawerRepo.rateApp(activity)
                null
            }
            R.id.support_Feedback -> {
                drawerRepo.reportBug(activity)
                null
            }
            R.id.support_Help -> HELLO_DIALOG
            else -> error("Unexpected NavigationDrawer id -> $itemId")
        }
    }
}
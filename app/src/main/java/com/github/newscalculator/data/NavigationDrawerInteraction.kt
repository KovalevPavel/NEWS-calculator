package com.github.newscalculator.data

import android.app.Activity

interface NavigationDrawerInteraction {
    fun shareApp(activity: Activity)
    fun rateApp(activity: Activity)
    fun requestHelp()
    fun reportBug(activity: Activity)
}
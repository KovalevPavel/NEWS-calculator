package com.github.ambulance10

import android.os.Bundle
import androidx.fragment.app.Fragment

fun <T: Fragment> T.withArgs (action: Bundle.() -> Unit) : T {
    val args = Bundle().apply (action)
    arguments = args
    return this
}
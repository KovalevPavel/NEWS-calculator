package com.github.newscalculator.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.newscalculator.databinding.ViewSynchronizingBinding

class SynchronizingCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var customBinder: ViewSynchronizingBinding =
        ViewSynchronizingBinding.inflate(LayoutInflater.from(context))

    init {
        addView(customBinder.root)
    }
}
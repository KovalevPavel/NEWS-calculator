package com.github.newscalculator.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.newscalculator.databinding.ItemBookmarkBinding

class BookmarkCustomView @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrSet, defStyleAttr) {
    var customBinder: ItemBookmarkBinding =
        ItemBookmarkBinding.inflate(LayoutInflater.from(context))

    init {
        addView(customBinder.root)
        customBinder.textValue.text = "0"
    }
}
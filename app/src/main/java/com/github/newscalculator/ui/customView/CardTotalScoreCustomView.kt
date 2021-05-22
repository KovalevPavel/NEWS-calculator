package com.github.newscalculator.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.github.newscalculator.databinding.ViewTotalScoreBinding
import com.google.android.material.card.MaterialCardView

/**
 * Контейнер, в котором отображается общая сумма баллов по пациенту.
 * @property customBinder Объект типа [ViewTotalScoreBinding] для осуществления viewBinding
 */
class CardTotalScoreCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    var customBinder: ViewTotalScoreBinding =
        ViewTotalScoreBinding.inflate(LayoutInflater.from(context))

    init {
        addView(customBinder.root)
    }
}
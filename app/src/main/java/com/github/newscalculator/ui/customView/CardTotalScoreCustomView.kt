package com.github.newscalculator.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import com.github.newscalculator.R
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

    /**
     * Устанавливаем общую сумму баллов в счетчик и устанавливаем для текста соответствующий цвет текста
     * @param newSum получившаяся сумма. Если на вход подать "-1", будет выведено начальное значение "__/19"
     */
    fun setTotalScore(newSum: Int) {
        val totalString = if (newSum == -1) "__/19" else "$newSum/19"
        customBinder.textTotalValue.text = totalString
        setTextColor()
    }

    /**
     * Устанавливаем для текста соответствующий текст
     */
    private fun setTextColor() {
        val lastIndex = customBinder.textTotalValue.text.indexOfFirst {
            it == '/'
        }

        val inputParameter =
            try {
                customBinder.textTotalValue.text.subSequence(0, lastIndex - 1).toString().toInt()
            } catch (e: Exception) {
                20
            }
        val color = when (inputParameter) {
            in (0 until 3) -> R.color.green
            in (3 until 6) -> R.color.yellow
            else -> R.color.red
        }
        customBinder.textTotalValue.setTextColor(
            ResourcesCompat.getColor(resources, color, null)
        )
    }
}
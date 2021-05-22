package com.github.newscalculator.ui.mainFragment.recyclerView

import androidx.recyclerview.widget.RecyclerView
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType

/**
 * <p>ViewHolder для отображения элементов списка измеряемых параметров.</p>
 * @param onItemClick - событие нажатие на элемент списка (открытие диалога редактирования).
 * @param onItemLongClick - событие долгого нажатия на элемент списка (очистка элемента списка).
 */
class DiseaseHolder(
    private val binder: ItemEvaluationParameterBinding,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binder.root) {

    init {
        binder.parentCard.setOnClickListener {
            onItemClick(adapterPosition)
        }

        binder.parentCard.setOnLongClickListener {
            onItemLongClick(adapterPosition)
            true
        }
    }

    fun bind(item: AbstractDiseaseType) {
        binder.apply {
            textParameterName.text = item.parameterName
            textViewEvalPoints.text = item.createMeasuredString()
            textView.text = if (textViewEvalPoints.text.isEmpty()) "" else item.createPointsString()
        }
    }
}
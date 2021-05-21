package com.github.newscalculator.ui.mainFragment.recyclerView

import androidx.recyclerview.widget.RecyclerView
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

class DiseaseHolder(
    private val binder: ItemEvaluationParameterBinding,
    onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binder.root) {

    init {
        itemView.setOnClickListener {
            onClick(adapterPosition)
        }
    }

    fun bind(item: AbstractDiseaseType, bindType: BindType) {
        binder.apply {
            textParameterName.text = item.parameterName
            textViewEvalPoints.text = when (bindType) {
                BindType.INITIAL -> ""
                BindType.REWRITE -> item.createMeasuredString()
            }
            revealBookmark(bindType, item.createPointsString())
        }

    }

    private fun revealBookmark(type: BindType, points: String) {
        if (type == BindType.REWRITE) {
            binder.bookmark.customBinder.textValue.text = points
            binder.root.transitionToEnd()
        } else {
            binder.root.transitionToStart()
        }
    }
/*
    класс, определяющий тип биндинга
    INITIAL - биндинг происходит в первый раз
    REWRITE - во второй и последующие разы
 */
    enum class BindType {
        INITIAL,
        REWRITE
    }
}
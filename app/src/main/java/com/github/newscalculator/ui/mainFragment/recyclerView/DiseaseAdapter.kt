package com.github.newscalculator.ui.mainFragment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType

/**
 * <p>Адаптер для списка измеряемых значений.</p>
 * @param onItemClick - событие нажатие на элемент списка (открытие диалога редактирования).
 * @param onItemLongClick - событие долгого нажатия на элемент списка (очистка элемента списка).
 * @property binder - объект [ItemEvaluationParameterBinding] для осуществления viewBinding.
 * @property diseaseList - список измеряемых параметров.
 */

class DiseaseAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) :
    RecyclerView.Adapter<DiseaseHolder>() {
    private lateinit var binder: ItemEvaluationParameterBinding
    var diseaseList = mutableListOf<AbstractDiseaseType>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseHolder {
        val inflater = LayoutInflater.from(parent.context)
        binder = ItemEvaluationParameterBinding.inflate(inflater, parent, false)
        return DiseaseHolder(binder, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: DiseaseHolder, position: Int) {
        val item = diseaseList[position]
        holder.bind(item)
    }

    override fun getItemCount() = diseaseList.size
}
package com.github.ambulance10.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.ambulance10.EvalParameter
import com.github.ambulance10.databinding.ItemEvaluationParameterBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class DiseaseDelegateAdapter(private val onItemClick: (Int) -> Unit) :
    AbsListItemAdapterDelegate<EvalParameter, EvalParameter, DiseaseDelegateAdapter.DiseaseHolder>() {

    override fun isForViewType(
        item: EvalParameter,
        items: MutableList<EvalParameter>,
        position: Int
    ) = true

    override fun onCreateViewHolder(parent: ViewGroup): DiseaseHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEvaluationParameterBinding.inflate(inflater, parent, false)
        return DiseaseHolder(
            binding.root, onItemClick
        )
    }

    override fun onBindViewHolder(
        item: EvalParameter,
        holder: DiseaseHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class DiseaseHolder(
        view: View,
        onClick: (Int) -> Unit
    ) : BaseHolder(view, onClick) {
        private val binder = DataBindingUtil.bind<ItemEvaluationParameterBinding>(view)

        fun bind(item: EvalParameter) {
            binder?.inputEvalParameter = item
        }
    }
}
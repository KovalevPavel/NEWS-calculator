package com.github.newscalculator.adapters

import androidx.recyclerview.widget.DiffUtil
import com.github.newscalculator.EvalParameter
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class EvalAdapter (onItemClick: (Int) -> Unit) :
    AsyncListDifferDelegationAdapter<EvalParameter>(EvalDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(DiseaseDelegateAdapter(onItemClick))
    }

    class EvalDiffUtilCallback : DiffUtil.ItemCallback<EvalParameter>() {
        override fun areItemsTheSame(oldItem: EvalParameter, newItem: EvalParameter) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EvalParameter, newItem: EvalParameter) =
            oldItem == newItem
    }
}
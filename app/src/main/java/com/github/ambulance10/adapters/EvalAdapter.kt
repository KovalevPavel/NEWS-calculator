package com.github.ambulance10.adapters

import androidx.recyclerview.widget.DiffUtil
import com.github.ambulance10.EvalParameter
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class EvalAdapter (onItemClick: (Int) -> Unit) :
    AsyncListDifferDelegationAdapter<EvalParameter>(EvalDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(DiseaseDelegateAdapter(onItemClick))
    }

    class EvalDiffUtilCallback : DiffUtil.ItemCallback<EvalParameter>() {
        override fun areItemsTheSame(oldItem: EvalParameter, newItem: EvalParameter) =
            oldItem.parameterName == newItem.parameterName

        override fun areContentsTheSame(oldItem: EvalParameter, newItem: EvalParameter) =
            oldItem == newItem
    }
}
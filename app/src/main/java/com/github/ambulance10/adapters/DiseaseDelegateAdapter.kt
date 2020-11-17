package com.github.ambulance10.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.github.ambulance10.EvalParameter
import com.github.ambulance10.databinding.ItemEvaluationParameterBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.synthetic.main.item_evaluation_parameter.view.*

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
            binder?.apply {
                inputEvalParameter = item
                Log.d("ZAWARUDO", "$adapterPosition: ${item.diseasePoints}")
                root.textViewEvalPoints?.text = makeEvalString(item)
                root.textViewDiseasePoints.text = when (item.diseaseBooleanPoints) {
                    0 -> "${item.diseasePoints}"
                    else -> if (item.diseasePoints != 0) "${item.diseasePoints}\n${item.diseaseBooleanPoints}" else "${item.diseaseBooleanPoints}"
                }
            }
        }

        private fun makeEvalString(item: EvalParameter): String {
            with(item) {
                return if (diseaseBooleanPoints == 0)
                    when (evalValue) {
                        0.0, -1.0 -> ""
                        else -> convertEvalValue(item)
                    } else
                    when (evalValue) {
                        -1.0 -> "${item.specialMark?.substring(0 until 5)}"
                        else -> "${convertEvalValue(item)}\n${item.specialMark?.substring(0 until 5)}"
                    }
            }
        }

        private fun convertEvalValue (item: EvalParameter) =
            when (item.normalValue) {
                36.6 -> item.evalValue.toString()
                else -> item.evalValue.toInt().toString()
            }
    }


}
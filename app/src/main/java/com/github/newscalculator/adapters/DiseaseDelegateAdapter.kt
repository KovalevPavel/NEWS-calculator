package com.github.newscalculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.newscalculator.EvalParameter
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding
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
                root.textViewEvalPoints?.text = makeEvalString(item)
                root.textViewDiseasePoints.text =
                    item.measuredValue?.let {
                        when (item.diseaseBooleanPoints) {
                            0 -> "${item.diseasePoints}"
                            else -> if (item.diseasePoints != 0) "${item.diseasePoints}\n${item.diseaseBooleanPoints}" else "${item.diseaseBooleanPoints}"
                        }
                    } ?: "0"
            }
        }

        private fun makeEvalString(item: EvalParameter) =
            when (item.measuredValue) {
                -1.0 ->
                    if (item.diseaseBooleanPoints != 0) item.specialMark?.substring(0, 5)
                    else ""
                else -> convertEvalValue(item)
            }

        private fun convertEvalValue(item: EvalParameter): String {
            var tempString = ""
            item.measuredValue?.let {
                tempString = when (item.normalValue) {
                    36.6 -> it.toString()
                    else -> if (insuffOrNot(item)) "${it.toInt()}\n${
                        item.specialMark?.substring(
                            0,
                            5
                        )
                    }" else "${it.toInt()}"
                }
            }
//            Log.d("ZAWARUDO","ID: ${item.id}; measuredParam: ${item.measuredValue}; tempString = $tempString")
            return tempString
        }

        private fun insuffOrNot(item: EvalParameter) =
            item.id == 1 && item.diseaseBooleanPoints != 0
    }
}
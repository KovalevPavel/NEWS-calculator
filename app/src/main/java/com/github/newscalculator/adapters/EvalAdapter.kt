package com.github.newscalculator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.newscalculator.EvalParameter
import com.github.newscalculator.databinding.ItemEvaluationParameterBinding

class EvalAdapter(private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<DiseaseHolder>() {
    private lateinit var binder: ItemEvaluationParameterBinding
    var diseaseList = mutableListOf<EvalParameter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseHolder {
        val inflater = LayoutInflater.from(parent.context)
        binder = ItemEvaluationParameterBinding.inflate(inflater, parent, false)
        return DiseaseHolder(binder, onItemClick)
    }

    override fun onBindViewHolder(holder: DiseaseHolder, position: Int) {
        holder.bind(diseaseList[position])
    }

    override fun getItemCount() = diseaseList.size
}
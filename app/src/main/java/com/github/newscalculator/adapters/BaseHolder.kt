package com.github.newscalculator.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseHolder (view: View, onItemClick : (Int) -> Unit): RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            onItemClick (adapterPosition)
        }
    }
}
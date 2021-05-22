package com.github.newscalculator.ui.editvalueDialog.dialogContents

import android.view.View
import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType

class DialogSwitch(
    override val binder: DialogEditValueBinding,
    override val item: AbstractDiseaseType
) : DialogContent(binder, item) {
    override fun setEditText() {
        textWatcher = null
        binder.editTextNumberLayout.visibility = View.GONE
    }

    override fun setTitle() {
        binder.textDialogParameterName.visibility = View.GONE
    }
}
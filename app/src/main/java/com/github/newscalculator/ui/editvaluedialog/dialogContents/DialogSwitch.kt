package com.github.newscalculator.ui.editvaluedialog.dialogContents

import android.view.View
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.ui.editvaluedialog.DialogContent

class DialogSwitch(
    override val binder: DialogEditvalueBinding,
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
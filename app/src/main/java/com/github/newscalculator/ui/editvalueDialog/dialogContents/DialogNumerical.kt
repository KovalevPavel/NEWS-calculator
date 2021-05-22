package com.github.newscalculator.ui.editvalueDialog.dialogContents

import android.view.View
import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType

class DialogNumerical(
    override val binder: DialogEditValueBinding,
    override val item: AbstractDiseaseType
) : DialogContent(binder, item) {

    override fun setSwitch() {
        binder.switchEvalBooleanParameter.visibility = View.GONE
    }
}
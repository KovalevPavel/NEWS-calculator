package com.github.newscalculator.ui.editvaluedialog.dialogContents

import android.view.View
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.ui.editvaluedialog.DialogContent

class DialogNumerical(
    override val binder: DialogEditvalueBinding,
    override val item: AbstractDiseaseType
) : DialogContent(binder, item) {

    override fun setSwitch() {
        binder.switchEvalBooleanParameter.visibility = View.GONE
    }
}
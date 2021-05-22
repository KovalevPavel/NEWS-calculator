package com.github.newscalculator.ui.editvaluedialog.dialogContents

import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.ui.editvaluedialog.DialogContent

class DialogCombine(
    override val binder: DialogEditvalueBinding,
    override val item: AbstractDiseaseType
) : DialogContent(binder, item)
package com.github.newscalculator.ui.editvalueDialog.dialogContents

import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType

class DialogCombine(
    override val binder: DialogEditValueBinding,
    override val item: AbstractDiseaseType
) : DialogContent(binder, item)
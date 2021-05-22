package com.github.newscalculator.ui.editvaluedialog

import android.view.View
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

abstract class DialogContent(
    open val binder: DialogEditvalueBinding,
    open val item: AbstractDiseaseType
) {
    var textWatcher: MyTextWatcher? = null

    fun setUI() {
        setTitle()
        setEditText()
        setSwitch()
    }

    open fun setEditText() {
        textWatcher = MyTextWatcher(binder, item)
        binder.editTextNumberLayout.visibility = View.VISIBLE
        binder.editTextNumberSigned.apply {
            addTextChangedListener(textWatcher)
            hint =
                if (item.fractional) item.normalValue.toString()
                else item.normalValue.toInt().toString()
        }
    }

    open fun setSwitch() {
        binder.switchEvalBooleanParameter.apply {
            visibility = View.VISIBLE
            text = item.shortString
            isChecked = item.getBooleanParameter
            setOnCheckedChangeListener { _, isChecked ->
                item.setBooleanParameter(isChecked)
            }
        }
    }

    open fun setTitle() {
        binder.textDialogParameterName.apply {
            visibility = View.VISIBLE
            text = item.parameterName
        }
    }
}
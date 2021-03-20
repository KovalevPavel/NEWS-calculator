package com.github.newscalculator.screens.editvaluedialog

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.github.newscalculator.databinding.DialogEditvalueBinding

class MyTextWatcher(
    private val binder: DialogEditvalueBinding,
    private val inputType: TextInputType = TextInputType.COMMON
) : TextWatcher {
    private var deleting = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        deleting = before > 0
    }

    override fun afterTextChanged(s: Editable) {
        if (inputType == TextInputType.TEMPERATURE && s.length == 2 && !deleting) {
            s.append(".")
        }

        binder.apply {
            val enteredValue = try {
                s.toString().toDouble()
            } catch (e: NumberFormatException) {
                if (inputType == TextInputType.OXYGEN) -1.0 else 0.0
            }
                switchEvalBooleanParameter.isEnabled =
                    if (switchEvalBooleanParameter.isVisible && enteredValue != (-1).toDouble()) enteredValue < (94.toDouble()) else false
                if (!switchEvalBooleanParameter.isEnabled) switchEvalBooleanParameter.isChecked =
                    false
        }
    }
}
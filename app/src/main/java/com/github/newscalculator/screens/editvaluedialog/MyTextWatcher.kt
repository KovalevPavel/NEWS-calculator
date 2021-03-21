package com.github.newscalculator.screens.editvaluedialog

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CombinedDiseaseType

/*
Класс отслеживает вводимые данные в EditText.
При достижении определенного количества введенных символов автоматически вводится '.'
 */

class MyTextWatcher(
    private val binder: DialogEditvalueBinding,
    private val diseaseParameter: AbstractDiseaseType,
) : TextWatcher {
    private var deleting = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        deleting = before > 0
    }

    override fun afterTextChanged(s: Editable) {
        if (diseaseParameter.fractional && s.length == 2 && !deleting) {
            s.append(".")
        }

        binder.apply {
            val enteredValue = try {
                s.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
            resetSwitch(enteredValue)
        }
    }

    private fun resetSwitch(enteredValue: Double) {
        binder.apply {
            if (switchEvalBooleanParameter.isVisible && diseaseParameter is CombinedDiseaseType)
                switchEvalBooleanParameter.isEnabled = enteredValue < diseaseParameter.threshold

            if (switchEvalBooleanParameter.isEnabled.not())
                switchEvalBooleanParameter.isChecked = false
        }
    }
}
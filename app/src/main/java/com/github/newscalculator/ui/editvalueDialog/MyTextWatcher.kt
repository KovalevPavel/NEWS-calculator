package com.github.newscalculator.ui.editvalueDialog

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.entities.CombinedDiseaseType


/**
 * Отслеживает вводимые в EditText данные
 * @property deleting Флаг, показывающий состояние ввода:
 * - false если пользователь вводит символы
 * - true если пользователь удаляет символы
 */
class MyTextWatcher(
    private val binder: DialogEditValueBinding,
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
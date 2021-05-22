package com.github.newscalculator.ui.editvalueDialog.dialogContents

import android.view.View
import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.ui.editvalueDialog.MyTextWatcher

/**
 * Абстрактный класс состояния.
 * Определяет состояние, в котором может находиться окно диалога.
 * @param binder Объект типа [DialogEditValueBinding] для осуществления viewBinding
 * @param item Определяет состояние окна диалога
 * @property textWatcher Переменная [MyTextWatcher].
 */
abstract class DialogContent(
    open val binder: DialogEditValueBinding,
    open val item: AbstractDiseaseType
) {
    var textWatcher: MyTextWatcher? = null

    /**
     * Установка внешнего вида диалога
     */
    fun setUI() {
        setTitle()
        setEditText()
        setSwitch()
    }

    /**
     * Установка внешнего вида текстового поля
     */
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

    /**
     * Установка внешнего вида переключателя
     */
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

    /**
     * Установка внешнего вида заголовка
     */
    open fun setTitle() {
        binder.textDialogParameterName.apply {
            visibility = View.VISIBLE
            text = item.parameterName
        }
    }
}
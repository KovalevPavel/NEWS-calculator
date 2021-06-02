package com.github.newscalculator.ui.editvalueDialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.MyApplication
import com.github.newscalculator.databinding.DialogEditValueBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.entities.CheckableDiseaseType
import com.github.newscalculator.domain.entities.EvalTypes
import com.github.newscalculator.ui.editvalueDialog.dialogContents.DialogCombine
import com.github.newscalculator.ui.editvalueDialog.dialogContents.DialogContent
import com.github.newscalculator.ui.editvalueDialog.dialogContents.DialogNumerical
import com.github.newscalculator.ui.editvalueDialog.dialogContents.DialogSwitch
import com.github.newscalculator.ui.mainFragment.ConnectionToDialog
import com.github.newscalculator.util.containsWrongChars
import com.github.newscalculator.util.truncation

/**
 * Диалог ввода/редактирования измеренного значения
 *
 * В методе [onStart] первым делом устанавливаем прозрачный цвет фона для контейнера диалога
 * Это необходимо, чтобы кастомная разметка отображалась корректно (иначе вокруг скруглений рамки будут белые острые кромки)
 * @property _binder Объект типа [DialogEditValueBinding] для осуществления viewBinding
 * @property args Аргументы, полученные от вызывающего объекта
 * @property parentEntity Объект, который вызвал данный диалог (активити или фрагмент)
 */
class EditValueDialog : DialogFragment() {
    private val newsUseCase = MyApplication.appComponent.getNEWSUseCase()
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var inputDiseaseParameter: AbstractDiseaseType
    private lateinit var dialogContentType: DialogContent

    private var _binder: DialogEditValueBinding? = null
    private val binder: DialogEditValueBinding
        get() = _binder!!

    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        _binder = DialogEditValueBinding.inflate(inflater)
        inputDiseaseParameter = args.inputEvalParameter
        setDialogContentType()
    }

    private fun setDialogContentType() {
        dialogContentType = when (inputDiseaseParameter.type) {
            EvalTypes.NUMERICAL -> DialogNumerical(binder, inputDiseaseParameter)
            EvalTypes.CHECKABLE -> DialogSwitch(binder, inputDiseaseParameter)
            EvalTypes.COMBINED -> DialogCombine(binder, inputDiseaseParameter)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogContentType.setUI()
        //обработка нажатия на кнопку
        binder.btnConfirm.setOnClickListener {
            val evalValue = convertEvalValue()
            parentEntity.onDialogClicked(
                inputDiseaseParameter,
                evalValue.truncation(),
                inputDiseaseParameter.getBooleanParameter
            )
            //скрытие диалога
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(binder.root)
            .create()
    }

    private fun convertEvalValue(): Double {
        binder.apply {
            return if (binder.editTextNumberSigned.text?.containsWrongChars(
                    arrayOf("", ".")
                ) == true
            ) inputDiseaseParameter.normalValue
            else editTextNumberSigned.text.toString().toDouble()
        }
    }

    override fun onResume() {
        super.onResume()
        showSoftInput()
    }

    /**
     * Автоматический показ виртуальной клавиатуры.
     */
    private fun showSoftInput() {
        if (inputDiseaseParameter is CheckableDiseaseType) return
        binder.apply {
            //утсанавливаем текст (если он ранее уже был введен)
            editTextNumberSigned.setText(newsUseCase.convertEvalValue(inputDiseaseParameter))
            //показываем клавиатуру
            editTextNumberSigned.requestFocus()
            editTextNumberSigned.setSelection(0, editTextNumberSigned.text?.length ?: 0)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binder = null
        //разрешаем вновь вызывать данный диалог
        parentEntity.allowToCallDialog = true
    }
}
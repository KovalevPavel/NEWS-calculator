package com.github.newscalculator.ui.editvaluedialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CheckableDiseaseType
import com.github.newscalculator.moshi.EvalTypes
import com.github.newscalculator.ui.editvaluedialog.dialogContents.DialogCombine
import com.github.newscalculator.ui.editvaluedialog.dialogContents.DialogNumerical
import com.github.newscalculator.ui.editvaluedialog.dialogContents.DialogSwitch
import com.github.newscalculator.ui.mainFragment.ConnectionToDialog
import com.github.newscalculator.util.containsWrongChars
import com.github.newscalculator.util.truncation

class EditValueDialog : DialogFragment() {
    private val repository = EditValueRepository()
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var inputDiseaseParameter: AbstractDiseaseType
    private lateinit var dialogContentType: DialogContent

    private var _binder: DialogEditvalueBinding? = null
    private val binder: DialogEditvalueBinding
        get() = _binder!!

    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        _binder = DialogEditvalueBinding.inflate(inflater)
        inputDiseaseParameter = args.inputEvalParameter
        setDialogContentType()
    }

    private fun setDialogContentType() {
        dialogContentType = when (inputDiseaseParameter.type) {
            EvalTypes.Numerical -> DialogNumerical(binder, inputDiseaseParameter)
            EvalTypes.Checkable -> DialogSwitch(binder, inputDiseaseParameter)
            EvalTypes.Combined -> DialogCombine(binder, inputDiseaseParameter)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogContentType.setUI()
        binder.btnConfirm.setOnClickListener {
            val evalValue = convertEvalValue()
            parentEntity.onDialogClicked(
                inputDiseaseParameter,
                evalValue.truncation(),
                inputDiseaseParameter.getBooleanParameter
            )
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

    private fun showSoftInput() {
        if (inputDiseaseParameter is CheckableDiseaseType) return
        binder.apply {
            editTextNumberSigned.setText(repository.convertEvalValue(inputDiseaseParameter))
            editTextNumberSigned.requestFocus()
            editTextNumberSigned.setSelection(0, editTextNumberSigned.text?.length ?: 0)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parentEntity.allowToCallDialog = true
    }
}
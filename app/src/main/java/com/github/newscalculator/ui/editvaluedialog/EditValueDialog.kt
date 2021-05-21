package com.github.newscalculator.ui.editvaluedialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CheckableDiseaseType
import com.github.newscalculator.diseaseparameterstypes.NumericalDiseaseType
import com.github.newscalculator.ui.mainFragment.ConnectionToDialog
import com.github.newscalculator.util.containsWrongChars
import com.github.newscalculator.util.logDebug
import com.github.newscalculator.util.truncation

class EditValueDialog : DialogFragment() {
    private val repository = EditValueRepository()
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var textWatcher: MyTextWatcher
    private lateinit var inputDiseaseParameter: AbstractDiseaseType

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
        textWatcher = MyTextWatcher(binder, inputDiseaseParameter)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        bindView()
        showSoftInput()
    }

    private fun bindView() {
        binder.apply {
            textDialogParameterName.text = inputDiseaseParameter.parameterName
            if (editTextNumberSigned.isVisible) setEditText()
            if (switchEvalBooleanParameter.isVisible) setSwitch()
            setDialogControls()
        }
    }

    private fun setEditText() {
        binder.apply {
            editTextNumberSigned.addTextChangedListener(textWatcher)
            editTextNumberSigned.hint =
                if (inputDiseaseParameter.fractional) inputDiseaseParameter.normalValue.toString()
                else inputDiseaseParameter.normalValue.toInt().toString()
        }
    }

    private fun setSwitch() {
        binder.apply {
            switchEvalBooleanParameter.text = inputDiseaseParameter.shortString
            switchEvalBooleanParameter.isChecked = inputDiseaseParameter.getBooleanParameter
            switchEvalBooleanParameter.setOnCheckedChangeListener { _, isChecked ->
                inputDiseaseParameter.setBooleanParameter(isChecked)
                logDebug("${inputDiseaseParameter.getBooleanParameter}")
            }
        }
    }

    private fun setDialogControls() {
        binder.apply {
            when (inputDiseaseParameter) {
                is NumericalDiseaseType -> {
                    switchEvalBooleanParameter.visibility = View.GONE
                }
                is CheckableDiseaseType -> {
                    editTextNumberSigned.visibility = View.GONE
                    textDialogParameterName.visibility = View.GONE
                }
            }
        }
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
package com.github.newscalculator.screens.editvaluedialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.databinding.DialogTitleBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.diseaseparameterstypes.CheckableDiseaseType
import com.github.newscalculator.diseaseparameterstypes.NumericalDiseaseType
import com.github.newscalculator.screens.mainfragment.ConnectionToDialog
import com.github.newscalculator.util.containsWrongChars
import com.github.newscalculator.util.logDebug
import com.github.newscalculator.util.truncation

class EditValueDialog : DialogFragment() {
    private val repository = EditValueRepository()
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var textWatcher: MyTextWatcher
    private lateinit var inputDiseaseParameter: AbstractDiseaseType

    private var _viewBinder: DialogEditvalueBinding? = null
    private var _titleBinder: DialogTitleBinding? = null
    private val viewBinder: DialogEditvalueBinding
        get() = _viewBinder!!
    private val titleBinder: DialogTitleBinding
        get() = _titleBinder!!

    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        _viewBinder = DialogEditvalueBinding.inflate(inflater)
        _titleBinder = DialogTitleBinding.inflate(inflater)
        inputDiseaseParameter = args.inputEvalParameter
        textWatcher = MyTextWatcher(viewBinder, inputDiseaseParameter)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(viewBinder.root)
            .setCustomTitle(titleBinder.root)
            .setPositiveButton("OK") { _, _ ->
                val evalValue = convertEvalValue()
                parentEntity.onDialogClicked(
                    inputDiseaseParameter,
                    evalValue.truncation(),
                    inputDiseaseParameter.getBooleanParameter
                )
            }.create()
    }

    private fun convertEvalValue(): Double {
        viewBinder.apply {
            return if (viewBinder.editTextNumberSigned.text.containsWrongChars(
                    arrayOf("", ".")
                )
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
        viewBinder.apply {
            textDialogParameterName.text = inputDiseaseParameter.parameterName
            if (editTextNumberSigned.isVisible) setEditText()
            if (switchEvalBooleanParameter.isVisible) setSwitch()
            setDialogControls()
        }
    }

    private fun setEditText() {
        viewBinder.apply {
            editTextNumberSigned.addTextChangedListener(textWatcher)
            editTextNumberSigned.hint =
                if (inputDiseaseParameter.fractional) inputDiseaseParameter.normalValue.toString()
                else inputDiseaseParameter.normalValue.toInt().toString()
        }
    }

    private fun setSwitch() {
        viewBinder.apply {
            switchEvalBooleanParameter.text = inputDiseaseParameter.shortString
            switchEvalBooleanParameter.isChecked = inputDiseaseParameter.getBooleanParameter
            switchEvalBooleanParameter.setOnCheckedChangeListener { _, isChecked ->
                inputDiseaseParameter.setBooleanParameter(isChecked)
                logDebug("${inputDiseaseParameter.getBooleanParameter}")
            }
        }
    }

    private fun setDialogControls() {
        viewBinder.apply {
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
        viewBinder.apply {
            editTextNumberSigned.setText(repository.convertEvalValue(inputDiseaseParameter))
            editTextNumberSigned.requestFocus()
            editTextNumberSigned.setSelection(0, editTextNumberSigned.text.length)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parentEntity.allowToCallDialog = true
    }
}
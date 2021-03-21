package com.github.newscalculator.screens.editvaluedialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.databinding.DialogEditvalueBinding
import com.github.newscalculator.databinding.DialogTitleBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.screens.mainfragment.ConnectionToDialog

class EditValueDialog : DialogFragment() {
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var viewBinder: DialogEditvalueBinding
    private lateinit var titleBinder: DialogTitleBinding
    private lateinit var textWatcher: MyTextWatcher
    private lateinit var inputDiseaseParameter: AbstractDiseaseType

    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        viewBinder = DialogEditvalueBinding.inflate(inflater)
        titleBinder = DialogTitleBinding.inflate(inflater)
        inputDiseaseParameter = args.inputEvalParameter
        textWatcher = when (inputDiseaseParameter.id) {
            1L -> MyTextWatcher(viewBinder, TextInputType.OXYGEN)
            2L -> MyTextWatcher(viewBinder, TextInputType.TEMPERATURE)
            else -> MyTextWatcher(viewBinder, TextInputType.COMMON)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinder.editTextNumberSigned.addTextChangedListener(textWatcher)
        return AlertDialog.Builder(requireContext())
            .setView(viewBinder.root)
            .setCustomTitle(titleBinder.root)
            .setPositiveButton("OK") { _, _ ->
                viewBinder.apply {
                    val isSwitchChecked = switchEvalBooleanParameter.isChecked
                    inputDiseaseParameter.apply {
                        val evalValue = when (editTextNumberSigned.text.toString()) {
                            "" -> if (!editTextNumberSigned.isVisible) (-1).toDouble() else normalValue
                            "." -> normalValue
                            else -> editTextNumberSigned.text.toString().toDouble()
                        }
                        parentEntity.onDialogClicked(
                            this,
                            evalValue.truncation(),
                            isSwitchChecked
                        )
                    }
                }
            }
            .create()
    }

    override fun onResume() {
        super.onResume()
        bindView()
        if (inputDiseaseParameter.id != 5L)
            dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentEntity.allowToCallDialog = true
    }

    private fun bindView() {
        viewBinder.apply {
            textDialogParameterName.text = inputDiseaseParameter.parameterName
            editTextNumberSigned.hint =
                if (inputDiseaseParameter.id == 2L) inputDiseaseParameter.normalValue.toString()
                else inputDiseaseParameter.normalValue.toInt().toString()
            switchEvalBooleanParameter.text = inputDiseaseParameter.shortString
            switchEvalBooleanParameter.isChecked = inputDiseaseParameter.measuredArray[1] as Boolean

            if (inputDiseaseParameter.id == 5L) {
                editTextNumberSigned.visibility = View.GONE
                textDialogParameterName.visibility = View.GONE
            }

            if (!(inputDiseaseParameter.id == 1L || inputDiseaseParameter.id == 5L))
                switchEvalBooleanParameter.visibility = View.GONE
            if (inputDiseaseParameter.id != 5L) {
                editTextNumberSigned.setText(convertEvalValue(inputDiseaseParameter))
                editTextNumberSigned.requestFocus()
                editTextNumberSigned.setSelection(0, editTextNumberSigned.text.length)
            }
        }
    }

    private fun Double.truncation(): Double {
        val dotPosition = this.toString().toCharArray().indexOfFirst {
            it == '.'
        }
        return this.toString().substring(0, dotPosition + 2).toDouble()
    }

    private fun convertEvalValue(item: AbstractDiseaseType): String {
        val measuredValue = item.measuredArray[0] as Double
        return when (item.normalValue) {
            36.6 -> if (measuredValue == 0.0) "" else measuredValue.toString()
            else -> if (measuredValue == 0.0) "" else measuredValue.toInt().toString()
        }
    }
}
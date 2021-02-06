package com.github.newscalculator

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

class EditValueDialog : DialogFragment() {
    private val args: EditValueDialogArgs by navArgs()
    private lateinit var viewBinder: DialogEditvalueBinding
    private lateinit var titleBinder: DialogTitleBinding
    private lateinit var textWatcher: MyTextWatcher
    private lateinit var inputEvalParameter: EvalParameter

    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(requireContext())
        viewBinder = DialogEditvalueBinding.inflate(inflater)
        titleBinder = DialogTitleBinding.inflate(inflater)
        inputEvalParameter = args.inputEvalParameter
        textWatcher = when (inputEvalParameter.id) {
            1 -> MyTextWatcher(viewBinder, TextInputType.OXYGEN)
            2 -> MyTextWatcher(viewBinder, TextInputType.TEMPERATURE)
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
                    inputEvalParameter.apply {
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
        if (inputEvalParameter.id != 5)
            dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentEntity.allowToCallDialog = true
    }

    private fun bindView() {
        viewBinder.apply {
            textDialogParameterName.text = inputEvalParameter.parameterName
            editTextNumberSigned.hint =
                if (inputEvalParameter.id == 2) inputEvalParameter.normalValue.toString()
                else inputEvalParameter.normalValue.toInt().toString()
            switchEvalBooleanParameter.text = inputEvalParameter.specialMark
            switchEvalBooleanParameter.isChecked = inputEvalParameter.diseaseBooleanPoints != 0

            if (inputEvalParameter.id == 5) {
                editTextNumberSigned.visibility = View.GONE
                textDialogParameterName.visibility = View.GONE
            }

            if (!(inputEvalParameter.id == 1 || inputEvalParameter.id == 5))
                switchEvalBooleanParameter.visibility = View.GONE
            if (inputEvalParameter.id != 5) {
                editTextNumberSigned.setText(convertEvalValue(inputEvalParameter))
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

    private fun convertEvalValue(item: EvalParameter?) =
        item?.measuredValue?.let {
            when (item.normalValue) {
                36.6 -> if (it == 0.0) "" else it.toString()
                else -> if (it == 0.0) "" else it.toInt().toString()
            }
        } ?: ""
}
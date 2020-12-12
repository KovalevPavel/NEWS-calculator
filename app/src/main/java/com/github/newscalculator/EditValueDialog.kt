package com.github.newscalculator

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.newscalculator.databinding.DialogEditvalueBinding
import kotlinx.android.synthetic.main.dialog_editvalue.*
import kotlinx.android.synthetic.main.dialog_editvalue.view.*


class EditValueDialog : DialogFragment() {

    private val args: EditValueDialogArgs by navArgs()

    private lateinit var binder: DialogEditvalueBinding
    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragmentManager.primaryNavigationFragment as ConnectionToDialog

    private var inputEvalParameter: EvalParameter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputEvalParameter = args.inputEvalParameter
        binder = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dialog_editvalue,
            null,
            false
        )
        binder.inputData = inputEvalParameter
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binder.root.editTextNumberSigned.addTextChangedListener(textWatcher)

        return AlertDialog.Builder(requireContext())
            .setView(binder.root)
            .setCustomTitle(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_title, null))
            .setPositiveButton("OK") { _, _ ->

                with(binder.root) {
                    val isSwitchChecked = switchEvalBooleanParameter.isChecked
                    inputEvalParameter?.let {
                        val evalValue = when (editTextNumberSigned.text.toString()) {
                            "" -> if (!editTextNumberSigned.isVisible) (-1).toDouble() else it.normalValue
                            "." -> it.normalValue
                            else -> editTextNumberSigned.text.toString().toDouble()
                        }
                        parentEntity.onDialogClicked(
                            it,
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
        dialog?.let {
            it.editTextNumberSigned.setText(convertEvalValue(inputEvalParameter))
            it.editTextNumberSigned?.requestFocus()
            it.editTextNumberSigned.setSelection(0, it.editTextNumberSigned.text.length)
            it.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable?) {
            with(binder.root) {
                val enteredValue = try {
                    s.toString().toDouble()
                } catch (e: NumberFormatException) {
                    0.0
                }
                switchEvalBooleanParameter.isEnabled =
                    if (switchEvalBooleanParameter.isVisible && enteredValue != (-1).toDouble()) enteredValue < (94.toDouble()) else false
                if (!switchEvalBooleanParameter.isEnabled) switchEvalBooleanParameter.isChecked =
                    false
            }
        }
    }

    private fun Double.truncation(): Double {
        val dotPosition = this.toString().toCharArray().indexOfFirst {
            it == '.'
        }
        return this.toString().substring(0,dotPosition+2).toDouble()
    }

    private fun convertEvalValue(item: EvalParameter?) =
        item?.measuredValue?.let {
            when (item.normalValue) {
                36.6 -> if (it==0.0) "" else it.toString()
                else -> if (it == 0.0) "" else it.toInt().toString()
            }
        } ?: ""
}
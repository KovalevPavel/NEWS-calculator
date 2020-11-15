package com.github.ambulance10

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.github.ambulance10.databinding.DialogEditvalueBinding
import kotlinx.android.synthetic.main.dialog_editvalue.view.*

class EditValueDialog : DialogFragment() {
    companion object {
        private const val INPUT_EVAL_PARAMETER = "input_evalParameter"

        fun newInstance(inputEvalParameter: EvalParameter) = EditValueDialog().withArgs {
            putParcelable(INPUT_EVAL_PARAMETER, inputEvalParameter)
        }
    }

    private lateinit var binder: DialogEditvalueBinding
    private val parentEntity: ConnectionToDialog
        get() = activity?.let {
            it as? ConnectionToDialog
        } ?: parentFragment as ConnectionToDialog

    private var inputEvalParameter: EvalParameter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputEvalParameter = requireArguments().getParcelable(
            INPUT_EVAL_PARAMETER
        )
        binder = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dialog_editvalue,
            null,
            false
        )

        binder.inputData = inputEvalParameter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binder.root.editTextNumberSigned.addTextChangedListener(textWatcher)

        return AlertDialog.Builder(requireContext())
            .setView(binder.root)
            .setPositiveButton("OK") { _, _ ->

                with(binder.root) {
                    val isSwitchChecked = switchEvalBooleanParameter.isChecked
                    inputEvalParameter?.let {
                        val evaledValue = when (editTextNumberSigned.text.toString()) {
                            "" -> if (!editTextNumberSigned.isVisible) (-1).toDouble() else it.normalValue
                            "." -> it.normalValue
                            else -> editTextNumberSigned.text.toString().toDouble()
                        }
                        parentEntity.onDialogClicked(
                            it,
                            evaledValue.trunc(),
                            isSwitchChecked
                        )
                    }
                }

            }
            .create()
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
                    (-1).toDouble()
                }
                switchEvalBooleanParameter.isEnabled =
                    if (switchEvalBooleanParameter.isVisible && enteredValue != (-1).toDouble()) enteredValue <= (93.toDouble()) else false
                if (!switchEvalBooleanParameter.isEnabled) switchEvalBooleanParameter.isChecked =
                    false
            }
        }
    }

    private fun Double.trunc(): Double {
        val tempInt = this.toInt()
        return if (this - tempInt == 0.0) this else
            "$tempInt.${((this - tempInt) * 10).toInt()}".toDouble()
    }

}
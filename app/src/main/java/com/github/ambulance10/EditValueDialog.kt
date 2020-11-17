package com.github.ambulance10

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.ambulance10.databinding.DialogEditvalueBinding
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binder.root.editTextNumberSigned.addTextChangedListener(textWatcher)

        return AlertDialog.Builder(requireContext())
            .setView(binder.root)
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
                    if (switchEvalBooleanParameter.isVisible && enteredValue != (-1).toDouble()) enteredValue < (94.toDouble()) else false
                if (!switchEvalBooleanParameter.isEnabled) switchEvalBooleanParameter.isChecked =
                    false
            }
        }
    }

    private fun Double.truncation(): Double {
        val tempInt = this.toInt()
        return if (this - tempInt == 0.0) this else
            "$tempInt.${((this - tempInt) * 10).toInt()}".toDouble()
    }

}
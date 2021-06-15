package com.github.newscalculator.ui.helloDialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.github.newscalculator.databinding.DialogHelloBinding

class DialogHello : DialogFragment() {
    private var _binder: DialogHelloBinding? = null
    private val binder: DialogHelloBinding
        get() = _binder!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binder = DialogHelloBinding.inflate(LayoutInflater.from(requireContext()))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder (requireContext())
            .setView(binder.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binder.btnRetry.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binder = null
    }
}
package com.github.newscalculator.ui.retryDialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.github.newscalculator.databinding.DialogRetrySyncBinding
import com.github.newscalculator.ui.mainFragment.ConnectionToRetryDialog

class DialogRetry : DialogFragment() {
    private var _binder: DialogRetrySyncBinding? = null
    private val binder: DialogRetrySyncBinding
        get() = _binder!!

    private val parentEntity: ConnectionToRetryDialog
        get() = activity?.let {
            it as? ConnectionToRetryDialog
        } ?: parentFragment as ConnectionToRetryDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binder = DialogRetrySyncBinding.inflate(LayoutInflater.from(requireContext()))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(binder.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binder.btnRetry.setOnClickListener {
            parentEntity.doOnRetry()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binder = null
    }
}
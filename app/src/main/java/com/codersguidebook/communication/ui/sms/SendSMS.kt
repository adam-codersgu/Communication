package com.codersguidebook.communication.ui.sms

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.fragment.app.DialogFragment
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.databinding.SendSmsBinding

class SendSMS(private val number: String?) : DialogFragment() {
    private var _binding: SendSmsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mainActivity = activity as MainActivity
        val inflater = mainActivity.layoutInflater
        _binding = SendSmsBinding.inflate(inflater)

        val builder = AlertDialog.Builder(mainActivity)
            .setView(binding.root)

        if (number != null) {
            val editable: Editable = SpannableStringBuilder(number)
            binding.number.text = editable
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.sendBtn.setOnClickListener {
            if (mainActivity.sendSMS(binding.number.text.toString(), binding.body.text.toString())) dismiss()
        }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

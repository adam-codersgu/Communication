package com.codersguidebook.communication.ui.sms

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.SMS
import com.codersguidebook.communication.databinding.ViewSmsBinding

class ViewSMS(private val sms: SMS) : DialogFragment() {

    private var _binding: ViewSmsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mainActivity = activity as MainActivity
        val inflater = mainActivity.layoutInflater
        _binding = ViewSmsBinding.inflate(inflater)

        val builder = AlertDialog.Builder(mainActivity)
            .setView(binding.root)

        binding.sender.text = sms.sender
        binding.body.text = sms.body

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.replyBtn.setOnClickListener {
            mainActivity.openDialog(SendSMS(sms.sender))
            dismiss()
        }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

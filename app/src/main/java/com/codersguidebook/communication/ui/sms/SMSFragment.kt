package com.codersguidebook.communication.ui.sms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.codersguidebook.communication.CommunicationViewModel
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.databinding.FragmentSmsBinding
import com.codersguidebook.communication.ui.sms.SMSAdapter

class SMSFragment : Fragment() {

    private var _binding: FragmentSmsBinding? = null
    private val communicationViewModel: CommunicationViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: SMSAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        adapter = SMSAdapter(mainActivity)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.callLog.adapter = adapter

        mainActivity.getTexts()

        communicationViewModel.texts.observe(viewLifecycleOwner) { texts ->
            adapter.texts = texts
            adapter.notifyDataSetChanged()
        }

        binding.fab.setOnClickListener {
            mainActivity.openDialog(SendSMS(null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
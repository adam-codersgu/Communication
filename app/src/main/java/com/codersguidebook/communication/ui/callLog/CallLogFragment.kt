package com.codersguidebook.communication.ui.callLog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.codersguidebook.communication.CommunicationViewModel
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.databinding.FragmentCallLogBinding

class CallLogFragment : Fragment() {

    private val communicationViewModel: CommunicationViewModel by activityViewModels()
    private var _binding: FragmentCallLogBinding? = null
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: CallLogAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallLogBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        adapter = CallLogAdapter(mainActivity)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.layoutManager = LinearLayoutManager(activity)
        binding.root.adapter = adapter

        mainActivity.getCallLogs()

        communicationViewModel.callLog.observe(viewLifecycleOwner) { log ->
            // Load the 10 most recent calls
            adapter.callLog = log.take(10)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
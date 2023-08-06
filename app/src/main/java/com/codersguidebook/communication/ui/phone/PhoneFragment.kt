package com.codersguidebook.communication.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.R
import com.codersguidebook.communication.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {

    private var _binding: FragmentPhoneBinding? = null
    private lateinit var mainActivity: MainActivity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backspace.setOnClickListener {
            removeDigit()
        }

        binding.one.setOnClickListener {
            addDigit("1")
        }

        binding.two.setOnClickListener {
            addDigit("2")
        }

        binding.three.setOnClickListener {
            addDigit("3")
        }

        binding.four.setOnClickListener {
            addDigit("4")
        }

        binding.five.setOnClickListener {
            addDigit("5")
        }

        binding.six.setOnClickListener {
            addDigit("6")
        }

        binding.seven.setOnClickListener {
            addDigit("7")
        }

        binding.eight.setOnClickListener {
            addDigit("8")
        }

        binding.nine.setOnClickListener {
            addDigit("9")
        }

        binding.star.setOnClickListener {
            addDigit("*")
        }

        binding.zero.setOnClickListener {
            addDigit("0")
        }

        binding.hash.setOnClickListener {
            addDigit("#")
        }

        binding.callButton.setOnClickListener {
            val number = binding.phoneNumber.text
            if (number.isNotBlank()) mainActivity.callNumber(number.toString())
            else Toast.makeText(mainActivity, getString(R.string.no_number), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDigit(digit: String) {
        val previousNumber = binding.phoneNumber.text.toString()
        val newNumber = previousNumber + digit
        binding.phoneNumber.text = newNumber
    }

    private fun removeDigit() {
        val previousNumber = binding.phoneNumber.text.toString()
        if (previousNumber.isEmpty()) return
        val newNumber = previousNumber.take(previousNumber.length - 1)
        binding.phoneNumber.text = newNumber
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
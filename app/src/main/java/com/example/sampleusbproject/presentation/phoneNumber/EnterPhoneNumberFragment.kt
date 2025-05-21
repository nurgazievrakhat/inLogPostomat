package com.example.sampleusbproject.presentation.phoneNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterPhoneNumberBinding
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.setLinkedText

class EnterPhoneNumberFragment: Fragment() {
    private var _binding: FragmentEnterPhoneNumberBinding? = null
    private val binding get() = _binding!!
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = PhoneType.getType(arguments?.getInt("phoneType") ?: 0)
        binding.tvPolicy.setLinkedText(
            baseText = requireContext().getString(R.string.text_policy_before_link),
            linkText = requireContext().getString(R.string.text_policy_first_link),
            textAfter = requireContext().getString(R.string.text_policy_after_link),
            secondLinkText = requireContext().getString(R.string.text_policy_second_link),
            textEnd = "",
            onSecondLinkClick = {

            },
            onLinkClick = {

            }
        )
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_enterPhoneNumberFragment_to_enterSmsCodeFragment)
        }
        binding.keypadGrid.setOnKeyClickListener {
            binding.etPhoneNumber.input(it)
        }
        binding.btnClear.setOnClickListener {
            binding.etPhoneNumber.clear()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
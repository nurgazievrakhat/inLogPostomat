package com.example.sampleusbproject.presentation.phoneNumber

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterPhoneNumberBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.makeToast
import com.example.sampleusbproject.utils.setLinkedText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPhoneNumberFragment: BaseViewModelFragment<EnterPhoneNumberViewModel, FragmentEnterPhoneNumberBinding>(
    R.layout.fragment_enter_number,
    EnterPhoneNumberViewModel::class.java,
    FragmentEnterPhoneNumberBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun initialize() {
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
    }

    override fun setupListeners() {
        binding.btnContinue.setOnClickListener {
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

    override fun setupSubscribers() {
        viewModel.errorEvent.observe(viewLifecycleOwner){
            makeToast(R.string.text_something_went_wrong)
        }
        viewModel.onSuccessEvent.observe(viewLifecycleOwner){
            commonViewModel.phoneNumber = binding.etPhoneNumber.getRawPhoneNumber()
            findNavController().navigate(R.id.action_enterPhoneNumberFragment_to_enterSmsCodeFragment)
        }

        binding.btnContinue.setOnClickListener {
            val isFilled = binding.etPhoneNumber.isFilled()
            if (!isFilled)
                binding.etPhoneNumber.showError = requireContext().getString(R.string.text_wrong_phone_number)
            else {
                commonViewModel.phoneNumber = binding.etPhoneNumber.getRawPhoneNumber()
                findNavController().navigate(R.id.action_enterPhoneNumberFragment_to_enterSmsCodeFragment)
//                viewModel.sendSmsCode(phone = binding.etPhoneNumber.getRawPhoneNumber())
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack(R.id.enterPhoneNumberFragment, false)
        }
    }
}
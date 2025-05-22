package com.example.sampleusbproject.presentation.smsCode

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSmsCodeBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterSmsCodeFragment: BaseViewModelFragment<EnterSmsCodeViewModel, FragmentSmsCodeBinding>(
    R.layout.fragment_sms_code,
    EnterSmsCodeViewModel::class.java,
    FragmentSmsCodeBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            if (code.length < 6)
                binding.etCodeInput.error = requireContext().getString(R.string.text_not_full_sms_code)
            else
                findNavController().navigate(R.id.action_enterSmsCodeFragment_to_receiverFragment)
//                viewModel.sendSmsCode(commonViewModel.phoneNumber, code)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupSubscribers() {
        binding.keypadGrid.setOnKeyClickListener {
            binding.etCodeInput.text.append(it)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner){
            if (true)
                binding.etCodeInput.error = requireContext().getString(R.string.text_wrong_sms_code)
            else
                makeToast(R.string.text_something_went_wrong)
        }
        viewModel.onSuccessEvent.observe(viewLifecycleOwner){
            findNavController().navigate(R.id.action_enterSmsCodeFragment_to_receiverFragment)
        }
    }

}
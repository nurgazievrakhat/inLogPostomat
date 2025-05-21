package com.example.sampleusbproject.presentation.smsCode

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSmsCodeBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterSmsCodeFragment: BaseViewModelFragment<EnterSmsCodeViewModel, FragmentSmsCodeBinding>(
    R.layout.fragment_sms_code,
    EnterSmsCodeViewModel::class.java,
    FragmentSmsCodeBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun initialize() {

    }

    override fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_enterSmsCodeFragment_to_receiverFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
package com.example.sampleusbproject.presentation.smsCode

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSmsCodeBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.makeToast
import com.example.sampleusbproject.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterSmsCodeFragment : BaseViewModelFragment<EnterSmsCodeViewModel, FragmentSmsCodeBinding>(
    R.layout.fragment_sms_code,
    EnterSmsCodeViewModel::class.java,
    FragmentSmsCodeBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun initialize() {
        binding.etCodeInput.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    override fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            if (code.length < 6)
                binding.tvError.visible()
            else
                viewModel.sendSmsCode(commonViewModel.phoneNumber, code)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupSubscribers() {
        binding.keypadGrid.setOnKeyClickListener {
            binding.etCodeInput.text.append(it)
            if (binding.tvError.isVisible)
                binding.tvError.gone()
        }
        binding.btnClear.setOnClickListener {
            if (binding.etCodeInput.text.isNotEmpty())
                binding.etCodeInput.text.delete(
                    binding.etCodeInput.text.length - 1,
                    binding.etCodeInput.text.length
                )
            if (binding.tvError.isVisible)
                binding.tvError.gone()
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            if (true)
                binding.tvError.visible()
            else
                makeToast(R.string.text_something_went_wrong)
        }
        viewModel.onSuccessEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_enterSmsCodeFragment_to_receiverFragment)
        }
    }

}
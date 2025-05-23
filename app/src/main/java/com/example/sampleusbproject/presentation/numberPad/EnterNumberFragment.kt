package com.example.sampleusbproject.presentation.numberPad

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterNumberBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterNumberFragment :
    BaseViewModelFragment<EnterNumberViewModel, FragmentEnterNumberBinding>(
        R.layout.fragment_enter_number,
        EnterNumberViewModel::class.java,
        FragmentEnterNumberBinding::inflate
    ) {

    override fun setupListeners() {
        binding.keypadGrid.setOnKeyClickListener {
            binding.etCodeInput.text.append(it)
        }
        binding.btnClear.setOnClickListener {
            if (binding.etCodeInput.text.isNotEmpty())
                binding.etCodeInput.text.delete(
                    binding.etCodeInput.text.length - 1,
                    binding.etCodeInput.text.length
                )
        }
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            if (code.length < 4)
                makeToast(R.string.text_wrong_sms_code)
            else
                viewModel.getOrderByPassword(code)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupSubscribers() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            if (it)
                makeToast(R.string.text_something_went_wrong)
            else
                makeToast(R.string.text_order_not_found)
        }
        viewModel.successEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.openedBoardFragment, bundleOf("cell" to it))
        }
    }

}
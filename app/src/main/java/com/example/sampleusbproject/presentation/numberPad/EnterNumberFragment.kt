package com.example.sampleusbproject.presentation.numberPad

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterNumberBinding
import com.example.sampleusbproject.domain.models.GetOrderError
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.makeToast
import com.example.sampleusbproject.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterNumberFragment :
    BaseViewModelFragment<EnterNumberViewModel, FragmentEnterNumberBinding>(
        R.layout.fragment_enter_number,
        EnterNumberViewModel::class.java,
        FragmentEnterNumberBinding::inflate
    ) {

    override fun initialize() {
        binding.etCodeInput.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    override fun setupListeners() {
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
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            if (code.length < 4)
                binding.tvError.visible()
            else
                viewModel.getOrderByPassword(code)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setupSubscribers() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            if (it is GetOrderError.Unexpected)
                makeToast(R.string.text_something_went_wrong)
            else
                binding.tvError.visible()
        }
        viewModel.successEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.openedBoardFragment, bundleOf("cell" to it))
        }
    }

}
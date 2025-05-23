package com.example.sampleusbproject.presentation.phoneNumber

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentEnterPhoneNumberBinding
import com.example.sampleusbproject.presentation.base.BaseFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.gone

class EnterReceiverPhoneNumberFragment : BaseFragment<FragmentEnterPhoneNumberBinding>(
    R.layout.fragment_enter_number,
    FragmentEnterPhoneNumberBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPolicy.gone()
        binding.tvTitle.text = requireContext().getString(R.string.text_enter_receiver_phone_number)
        binding.keypadGrid.setOnKeyClickListener {
            binding.etPhoneNumber.input(it)
        }
        binding.btnClear.setOnClickListener {
            binding.etPhoneNumber.clear()
        }
        Log.e("fddfgdfgfdg", "onViewCreated: ${commonViewModel.receiverPhoneNumber}", )
        binding.btnContinue.setOnClickListener {
            val isFilled = binding.etPhoneNumber.isFilled()
            if (!isFilled)
                binding.etPhoneNumber.showError = requireContext().getString(R.string.text_wrong_phone_number)
            else{
                commonViewModel.receiverPhoneNumber = binding.etPhoneNumber.getRawPhoneNumber()
                findNavController().navigate(R.id.action_enterReceiverPhoneNumberFragment_to_confirmPhoneNumberDialogFragment)
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack(R.id.enterPhoneNumberFragment, false)
        }
    }

}
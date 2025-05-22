package com.example.sampleusbproject.presentation.dialogs.phoneNumber

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.DialogConfirmPhoneNumberBinding
import com.example.sampleusbproject.presentation.base.NonClosableDialogFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class ConfirmPhoneNumberDialogFragment: NonClosableDialogFragment<DialogConfirmPhoneNumberBinding>(
    DialogConfirmPhoneNumberBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private lateinit var phoneNumberFormatWatcher: FormatWatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+996 ___ __ __ __")
        val inputMask = MaskImpl.createTerminated(slots).apply {
            isHideHardcodedHead = true
        }
        phoneNumberFormatWatcher = MaskFormatWatcher(inputMask)
        phoneNumberFormatWatcher.installOn(binding.tvPhone)
        binding.tvPhone.text = commonViewModel.receiverPhoneNumber.removePrefix("996")

        binding.btnOk.setOnClickListener {
            findNavController().navigate(R.id.action_confirmPhoneNumberDialogFragment_to_selectCellFragment)
        }
        binding.ivClose.setOnClickListener {
            onBack()
        }
        binding.btnBack.setOnClickListener {
            onBack()
        }
    }

    private fun onBack(){
        findNavController().popBackStack()
    }

}
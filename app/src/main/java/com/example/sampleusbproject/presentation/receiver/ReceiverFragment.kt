package com.example.sampleusbproject.presentation.receiver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentReceiverBinding
import com.example.sampleusbproject.presentation.base.BaseFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.getColorStateList
import com.example.sampleusbproject.utils.makeToast
import com.google.android.material.button.MaterialButton

class ReceiverFragment : BaseFragment<FragmentReceiverBinding>(
    R.layout.fragment_receiver,
    FragmentReceiverBinding::inflate
) {

    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var self: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAnother.setOnClickListener {
            deselect(binding.btnSelf)
            selected(binding.btnAnother)
            self = false
        }
        binding.btnSelf.setOnClickListener {
            deselect(binding.btnAnother)
            selected(binding.btnSelf)
            self = true
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            if (self == null)
                makeToast(R.string.text_choose_receiver)
            else {
                val receiverPhone = if (self!!) commonViewModel.phoneNumber else ""
                commonViewModel.receiverPhoneNumber = receiverPhone
                if (self!!)
                    findNavController().navigate(R.id.action_receiverFragment_to_selectCellFragment)
                else
                    findNavController().navigate(R.id.action_receiverFragment_to_enterReceiverPhoneNumberFragment2)
            }
        }
    }

    fun selected(btn: MaterialButton) {
        btn.strokeColor = getColorStateList(R.color.blue_baby)
        btn.backgroundTintList = getColorStateList(R.color.blue_hovered)
        btn.setTextColor(getColorStateList(R.color.white))
    }

    fun deselect(btn: MaterialButton) {
        btn.strokeColor = getColorStateList(R.color.black_10)
        btn.backgroundTintList = getColorStateList(R.color.blue_light)
        btn.setTextColor(getColorStateList(R.color.shadow_black))
    }

}
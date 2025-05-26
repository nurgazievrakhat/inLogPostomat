package com.example.sampleusbproject.presentation.days

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentChoosePayerAndDaysBinding
import com.example.sampleusbproject.presentation.base.BaseFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.google.android.material.button.MaterialButton

class ChoosePayerAndDaysFragment : BaseFragment<FragmentChoosePayerAndDaysBinding>(
    R.layout.fragment_choose_payer_and_days,
    FragmentChoosePayerAndDaysBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var selfPayer: Boolean = true
    private var isClickable = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (selfPayer)
            selectSelfBtn()
        else
            selectOtherBtn()

        binding.btnSelf.setOnClickListener {
            selectSelfBtn()
        }
        binding.btnOther.setOnClickListener {
            selectOtherBtn()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack(R.id.receiverFragment, false)
        }
        binding.btnContinue.setOnClickListener {
            if (selfPayer) {
                findNavController().navigate(R.id.action_choosePayerAndDaysFragment_to_selectCellWithAmountFragment)
            } else {
                findNavController().navigate(R.id.action_choosePayerAndDaysFragment_to_selectCellFragment)
            }
        }
    }

    private fun selectSelfBtn() {
        selectBtn(binding.btnSelf)
        deselectBtn(binding.btnOther)
        selfPayer = true
        isClickable = false
    }

    private fun selectOtherBtn() {
        selectBtn(binding.btnOther)
        deselectBtn(binding.btnSelf)
        selfPayer = false
        isClickable = true
    }

    private fun setAlphaDaysGroup(alpha: Float) {
        binding.tvDaysInfo.alpha = alpha
        binding.tvDaysInfo.alpha = alpha
//        binding.rvDays.alpha = alpha
//        if (alpha < 1f)
//            binding.rvDays.isClickable = false
    }

    private fun selectBtn(btn: MaterialButton) {
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        btn.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_tight_bold_700)
        btn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_baby))
        btn.strokeWidth = 0
    }

    private fun deselectBtn(btn: MaterialButton) {
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.shadow_black))
        btn.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_tight_regular_400)
        btn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_light))
        btn.strokeWidth = 1
        btn.strokeColor =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_10))
    }

}
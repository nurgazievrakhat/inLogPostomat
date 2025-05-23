package com.example.sampleusbproject.presentation.days

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentChoosePayerAndDaysBinding
import com.example.sampleusbproject.presentation.base.BaseFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.days.adapter.PayerDays
import com.example.sampleusbproject.presentation.days.adapter.SelectDay
import com.example.sampleusbproject.presentation.days.adapter.SelectDayAdapter
import com.example.sampleusbproject.presentation.days.adapter.getSelectList
import com.example.sampleusbproject.utils.makeToast
import com.google.android.material.button.MaterialButton

class ChoosePayerAndDaysFragment : BaseFragment<FragmentChoosePayerAndDaysBinding>(
    R.layout.fragment_choose_payer_and_days,
    FragmentChoosePayerAndDaysBinding::inflate
) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private val adapter: SelectDayAdapter by lazy {
        SelectDayAdapter(this::onDayClick)
    }
    private var prevSelectedPos: Int = -1
    private var selfPayer: Boolean = true
    private var isClickable = true

    private fun onDayClick(pos: Int, model: SelectDay) {
        if (isClickable) {
            if (prevSelectedPos == pos)
                return

            if (pos > -1) {
                select(true, pos)
            }

            if (prevSelectedPos > -1) {
                select(false, prevSelectedPos)
            }

            prevSelectedPos = pos
        }
    }

    private fun select(isSelected: Boolean, pos: Int) {
        val list = adapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        adapter.notifyItemChanged(pos)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDays.adapter = adapter
        if (prevSelectedPos != -1)
            select(true, prevSelectedPos)

        adapter.submitList(PayerDays.RECEIVER.getSelectList(prevSelectedPos))

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
                val selectedDay = adapter.currentList.find { it.isSelected }?.day
                if (selectedDay != null) {
                    prevSelectedPos = -1
                    commonViewModel.days = selectedDay
                    findNavController().navigate(R.id.action_choosePayerAndDaysFragment_to_selectCellFragment)
                } else {
                    makeToast(R.string.text_choose_day_error)
                }
            }
        }
    }

    private fun selectSelfBtn() {
        selectBtn(binding.btnSelf)
        deselectBtn(binding.btnOther)
        selfPayer = true
        isClickable = false
        setAlphaDaysGroup(0.4f)
    }

    private fun selectOtherBtn() {
        selectBtn(binding.btnOther)
        deselectBtn(binding.btnSelf)
        selfPayer = false
        isClickable = true
        setAlphaDaysGroup(1f)
    }

    private fun setAlphaDaysGroup(alpha: Float) {
        binding.tvDaysInfo.alpha = alpha
        binding.tvDaysInfo.alpha = alpha
        binding.rvDays.alpha = alpha
        if (alpha < 1f)
            binding.rvDays.isClickable = false
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
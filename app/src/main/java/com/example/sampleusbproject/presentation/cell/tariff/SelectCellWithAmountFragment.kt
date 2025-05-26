package com.example.sampleusbproject.presentation.cell.tariff

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSelectCellWithAmountBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.cell.CenterItemDecoration
import com.example.sampleusbproject.presentation.cell.SelectCellAdapter
import com.example.sampleusbproject.presentation.cell.SelectCellModel
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.presentation.days.adapter.PayerDays
import com.example.sampleusbproject.presentation.days.adapter.SelectDay
import com.example.sampleusbproject.presentation.days.adapter.SelectDayAdapter
import com.example.sampleusbproject.presentation.days.adapter.getSelectList
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCellWithAmountFragment :
    BaseViewModelFragment<SelectCellWithAmountViewModel, FragmentSelectCellWithAmountBinding>(
        R.layout.fragment_select_free_cell,
        SelectCellWithAmountViewModel::class.java,
        FragmentSelectCellWithAmountBinding::inflate
    ) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var prevCellSelectedPos: Int = -1
    private var prevDaySelectedPos: Int = -1

    private val adapter: SelectCellAdapter by lazy {
        SelectCellAdapter(this::onClick, true)
    }

    private val daysAdapter: SelectDayAdapter by lazy {
        SelectDayAdapter(this::onDayClick)
    }

    private fun onDayClick(pos: Int, model: SelectDay) {
        if (prevDaySelectedPos == pos)
            return

        if (pos > -1) {
            onDaySelect(true, pos)
        }

        if (prevDaySelectedPos > -1) {
            onDaySelect(false, prevDaySelectedPos)
        }

        prevDaySelectedPos = pos
        updateSum()
    }

    private fun onDaySelect(isSelected: Boolean, pos: Int) {
        val list = daysAdapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        daysAdapter.notifyItemChanged(pos)
    }

    private fun onClick(pos: Int, model: SelectCellModel) {
        if (prevCellSelectedPos == pos)
            return

        if (prevCellSelectedPos > -1) {
            select(false, prevCellSelectedPos)
        }

        select(true, pos)
        prevCellSelectedPos = pos
        updateSum()
    }

    private fun select(isSelected: Boolean, pos: Int) {
        val list = adapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        adapter.notifyItemChanged(pos)
    }

    private fun updateSum(){
        val selectedDay = daysAdapter.currentList.find { it.isSelected }
        val selectedCell = adapter.currentList.find { it.isSelected }
        if (selectedDay != null && selectedCell != null){
            binding.tvAmount.text = "${selectedDay.day * selectedCell.boardSize.amount} сом"
        }
    }

    override fun initialize() {
        viewModel.getFreCells()
        binding.rvCells.adapter = adapter
        binding.rvCells.addItemDecoration(CenterItemDecoration())
        binding.rvDays.adapter = daysAdapter
        if (prevDaySelectedPos != -1)
            onDaySelect(true, prevDaySelectedPos)
        daysAdapter.submitList(PayerDays.SENDER.getSelectList(prevDaySelectedPos))
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            val selected = adapter.currentList.find { it.isSelected }
            val selectedDay = daysAdapter.currentList.find { it.isSelected }
            val sumOfPay = binding.tvAmount.text.toString().filter { it.isDigit() }.ifBlank { "0" }.toInt()

            if (selected == null) {
                makeToast(R.string.text_choose_size_error)
                return@setOnClickListener
            }

            if (selectedDay == null) {
                makeToast(R.string.text_choose_day_error)
                return@setOnClickListener
            }

            commonViewModel.days = selectedDay.day
            commonViewModel.selectedCell = SelectedCell(
                selected.cellId ?: "",
                number = selected.number ?: 0L
            )
            commonViewModel.sumOfPay = sumOfPay
            viewModel.createOrder(
                selected.cellId ?: "",
                commonViewModel.phoneNumber,
                commonViewModel.receiverPhoneNumber,
                commonViewModel.days
            )
        }
    }

    override fun setupSubscribers() {
        viewModel.createSuccessEvent.observe(viewLifecycleOwner){
            prevCellSelectedPos = -1
            prevDaySelectedPos = -1
            findNavController().navigate(R.id.action_selectCellWithAmountFragment_to_leaveParcelOpenedBoardFragment)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            makeToast(R.string.text_something_went_wrong)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.freeCells.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

}
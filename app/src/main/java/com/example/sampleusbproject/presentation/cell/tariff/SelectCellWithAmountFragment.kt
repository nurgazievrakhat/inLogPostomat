package com.example.sampleusbproject.presentation.cell.tariff

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSelectCellWithAmountBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.cell.CenterItemDecoration
import com.example.sampleusbproject.presentation.cell.SelectCellAdapter
import com.example.sampleusbproject.presentation.cell.SelectCellModel
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.presentation.days.adapter.PayerDays
import com.example.sampleusbproject.presentation.days.adapter.SelectDay
import com.example.sampleusbproject.presentation.days.adapter.SelectDayAdapter
import com.example.sampleusbproject.presentation.days.adapter.getSelectList
import com.example.sampleusbproject.utils.gone
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

    private lateinit var adapter: SelectCellAdapter

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

    private fun updateSum() {
        val selectedDay = daysAdapter.currentList.find { it.isSelected }
        val selectedCell = adapter.currentList.find { it.isSelected }
        if (selectedDay != null && selectedCell != null){
            binding.tvAmount.text = "${selectedDay.day * selectedCell.boardSize.amount} сом"
        }
    }

    override fun initialize() {
        adapter = SelectCellAdapter(this::onClick, false)
        viewModel.getFreCells(commonViewModel.selectedCell?.size)
        binding.rvCells.adapter = adapter
        binding.rvCells.addItemDecoration(CenterItemDecoration())
        binding.rvDays.adapter = daysAdapter
        daysAdapter.submitList(PayerDays.SENDER.getSelectList(commonViewModel.days))
        if (commonViewModel.orderId.isNotBlank())
            binding.btnBack.gone()
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            val selected = adapter.currentList.find { it.isSelected }
            val selectedDay = daysAdapter.currentList.find { it.isSelected }

            if (selected == null) {
                makeToast(R.string.text_choose_size_error)
                return@setOnClickListener
            }

            if (selectedDay == null) {
                makeToast(R.string.text_choose_day_error)
                return@setOnClickListener
            }

            if (commonViewModel.orderId.isBlank())
                viewModel.createOrder(
                    selected.cellId ?: "",
                    commonViewModel.phoneNumber,
                    commonViewModel.receiverPhoneNumber,
                    selectedDay.day
                )
            else
                viewModel.updateCell(
                    selected.cellId ?: "",
                    commonViewModel.orderId,
                    selectedDay.day
                )
        }
    }

    override fun setupSubscribers() {
        viewModel.createSuccessEvent.observe(viewLifecycleOwner) {
            commonViewModel.orderId = it
            saveData()
            findNavController().navigate(R.id.action_selectCellWithAmountFragment_to_leaveParcelOpenedBoardFragment)
        }
        viewModel.updateSuccessEvent.observe(viewLifecycleOwner) {
            saveData()
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

    fun saveData() {
        val selected = adapter.currentList.find { it.isSelected }
        val selectedDay = daysAdapter.currentList.find { it.isSelected }

        commonViewModel.days = selectedDay?.day ?: 0
        commonViewModel.selectedCell = SelectedCell(
            selected?.cellId ?: "",
            number = selected?.number ?: 0L,
            size = selected?.boardSize ?: BoardSize.S
        )
        val sumOfPay =
            binding.tvAmount.text.toString().filter { it.isDigit() }.ifBlank { "0" }.toInt()
        commonViewModel.sumOfPay = sumOfPay

        prevCellSelectedPos = -1
        prevDaySelectedPos = -1
    }

}
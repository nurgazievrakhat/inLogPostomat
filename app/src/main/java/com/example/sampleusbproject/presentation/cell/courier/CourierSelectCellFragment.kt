package com.example.sampleusbproject.presentation.cell.courier

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSelectFreeCellBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.cell.CenterItemDecoration
import com.example.sampleusbproject.presentation.cell.SelectCellAdapter
import com.example.sampleusbproject.presentation.cell.SelectCellModel
import com.example.sampleusbproject.presentation.commonViewModel.CourierViewModel
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.utils.gone
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourierSelectCellFragment :
    BaseViewModelFragment<CourierSelectCellViewModel, FragmentSelectFreeCellBinding>(
        R.layout.fragment_select_free_cell,
        CourierSelectCellViewModel::class.java,
        FragmentSelectFreeCellBinding::inflate
    ) {
    private val commonViewModel: CourierViewModel by navGraphViewModels(R.id.courier_navigation)

    private var prevSelectedPos: Int = -1

    private lateinit var adapter: SelectCellAdapter

    private fun onClick(pos: Int, model: SelectCellModel) {
        if (prevSelectedPos == pos)
            return

        if (prevSelectedPos > -1) {
            select(false, prevSelectedPos)
        }

        select(true, pos)
        prevSelectedPos = pos
    }

    private fun select(isSelected: Boolean, pos: Int) {
        val list = adapter.currentList.toMutableList()
        list[pos].isSelected = isSelected
        adapter.notifyItemChanged(pos)
    }

    override fun initialize() {
        adapter = SelectCellAdapter(this::onClick, false)
        viewModel.getFreCells(commonViewModel.cell?.size)
        binding.rvCells.adapter = adapter
        binding.rvCells.addItemDecoration(CenterItemDecoration())
        if (commonViewModel.cell != null)
            binding.btnBack.gone()
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            val selected = adapter.currentList.find { it.isSelected }

            if (selected == null) {
                makeToast(R.string.text_choose_size_error)
                return@setOnClickListener
            }

            if (commonViewModel.cell != null)
                viewModel.updateCell(
                    orderId = commonViewModel.orderId,
                    cellId = selected.cellId ?: ""
                )
            else
                viewModel.deliveryOrder(
                    orderId = commonViewModel.orderId,
                    selected.cellId ?: ""
                )
        }
    }

    override fun setupSubscribers() {
        viewModel.createSuccessEvent.observe(viewLifecycleOwner) {
            saveData()
            findNavController().navigate(R.id.openedCourierBoardFragment)
        }
        viewModel.updateSuccessEvent.observe(viewLifecycleOwner) {
            saveData()
            findNavController().navigate(R.id.openedCourierBoardFragment)
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

    private fun saveData(){
        val selected = adapter.currentList.find { it.isSelected }
        commonViewModel.cell = SelectedCell(
            selected?.cellId ?: "",
            number = selected?.number ?: 0L,
            size = selected?.boardSize ?: BoardSize.S
        )
        prevSelectedPos = -1
    }

}
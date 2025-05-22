package com.example.sampleusbproject.presentation.cell

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentSelectFreeCellBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCellFragment :
    BaseViewModelFragment<SelectCellViewModel, FragmentSelectFreeCellBinding>(
        R.layout.fragment_select_free_cell,
        SelectCellViewModel::class.java,
        FragmentSelectFreeCellBinding::inflate
    ) {
    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var prevSelectedPos: Int = -1

    private val adapter: SelectCellAdapter by lazy {
        SelectCellAdapter(this::onClick)
    }

    private fun onClick(pos: Int, model: SelectCellModel) {
        if (prevSelectedPos == pos)
            return

        if (prevSelectedPos > -1) {
            select(false, prevSelectedPos, model)
        }

        select(true, pos, model)
    }

    private fun select(isSelected: Boolean, pos: Int, model: SelectCellModel) {
        val list = adapter.currentList.toMutableList()
        list[pos] = model.copy(isSelected = isSelected)
        adapter.submitList(list)
        adapter.notifyItemChanged(pos)
    }

    override fun initialize() {
        viewModel.getFreCells()
        binding.rvCells.adapter = adapter
        binding.rvCells.addItemDecoration(CenterItemDecoration())
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack(R.id.receiverFragment, false)
        }
        binding.btnContinue.setOnClickListener {
            val selected = adapter.currentList.find { it.isSelected }
            if (selected != null) {
                commonViewModel.selectedCell = SelectedCell(
                    selected.cellId ?: "",
                    number = selected.number ?: 0L
                )
                findNavController().navigate(R.id.action_selectCellFragment_to_leaveParcelOpenedBoardFragment)
            }
        }
    }

    override fun setupSubscribers() {
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
package com.example.sampleusbproject.presentation.boards

import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.numberPad.PackageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveParcelOpenedBoardFragment :
    BaseViewModelFragment<LeaveParcelOpenedBoardViewModel, FragmentOpenedBoardBinding>(
        R.layout.fragment_opened_board,
        LeaveParcelOpenedBoardViewModel::class.java,
        FragmentOpenedBoardBinding::inflate
    ) {

    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter(commonViewModel.selectedCell?.number ?: 0)
    }

    override fun initialize() {
        binding.btnContinue.text = requireContext().getString(R.string.text_leave_parcel)
        binding.tvTitle.text = String.format(
            requireContext().getString(R.string.text_opened_board),
            (commonViewModel.selectedCell?.number ?: 0).toString()
        )

        binding.rvBoards.adapter = adapter
        binding.rvBoards.isNestedScrollingEnabled = false
        val dividerItemDecoration =
            BoardsDividerItemDecoration(
                requireContext(),
                5,
                5,
                RecyclerView.HORIZONTAL,
                R.drawable.board_divider
            )
        binding.rvBoards.addItemDecoration(dividerItemDecoration)
    }

    override fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_leaveParcelOpenedBoardFragment_to_anotherLeaveBoardDialogFragment)
        }
        binding.btnContinue.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host).navigate(
                R.id.global_action_to_success_fragment,
                bundleOf(
                    "type" to PackageType.getInt(
                        PackageType.TAKE
                    )
                )
            )
        }
    }

    override fun setupSubscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postomat.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

}
package com.example.sampleusbproject.presentation.boards

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.domain.models.LockStatus
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.presentation.numberPad.PackageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LeaveParcelOpenedBoardFragment :
    BaseViewModelFragment<LeaveParcelOpenedBoardViewModel, FragmentOpenedBoardBinding>(
        R.layout.fragment_opened_board,
        LeaveParcelOpenedBoardViewModel::class.java,
        FragmentOpenedBoardBinding::inflate
    ) {

    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var isFromClick = false

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter(commonViewModel.selectedCell?.number ?: -1)
    }

    override fun initialize() {
        if (commonViewModel.selectedCell != null) {
            Log.e("sdfsdf", "initialize: ${commonViewModel.selectedCell!!.number.toInt()}")
            (requireActivity() as? MainActivity)?.viewModel?.openLocker(
                1,
                commonViewModel.selectedCell!!.number.toInt()
            )
        } else {
            Log.e("sdfsdf", "initialize: ss ${-1}")
        }

        (requireActivity() as? MainActivity)?.viewModel?.observeLockerBoardEvents()
            ?.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
                        if (isFromClick && event.locker == commonViewModel.selectedCell?.number?.toInt()) {
                            isFromClick = false
                            if (event.status == LockStatus.CLOSED) {
                                findNavController().navigate(R.id.action_leaveParcelOpenedBoardFragment_to_anotherLeaveBoardDialogFragment)
                            } else {
                                findNavController().navigate(R.id.anotherLeaveCloseCellDialogFragment)
                            }
                        }
                        Timber.tag("Flow")
                            .d("Статус Дверь: ${event.locker}, статус: ${event.status}")
                    }

                    is LockerBoardResponse.Error -> {
                        Timber.tag("Flow").e("Ошибка: ${event.message}")
                    }

                    is LockerBoardResponse.OpenDoor -> {
                        Timber.tag("Flow").d("Дверь: ${event.locker}, статус: ${event.status}")
                    }

                    else -> Timber.tag("Flow").d("Получено: $event")
                }
            }

        binding.btnBack.text = requireContext().getString(R.string.text_choose_another_cell)
        binding.btnContinue.text = requireContext().getString(R.string.text_leave_parcel)
        binding.tvBoardNumber.text = (commonViewModel.selectedCell?.number ?: 0).toString()
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
            if (commonViewModel.selectedCell != null) {
                isFromClick = true
                (requireActivity() as? MainActivity)?.viewModel?.getCellStatus(
                    1,
                    commonViewModel.selectedCell!!.number.toInt()
                )
            }
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
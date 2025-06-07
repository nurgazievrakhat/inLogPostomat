package com.example.sampleusbproject.presentation.boards.courier

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
import com.example.sampleusbproject.data.reposityImpl.OpenDoorResponse
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.domain.models.LockStatus
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.commonViewModel.CourierViewModel
import com.example.sampleusbproject.presentation.numberPad.PackageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class OpenedCourierBoardFragment :
    BaseViewModelFragment<OpenedCourierBoardViewModel, FragmentOpenedBoardBinding>(
        R.layout.fragment_opened_board,
        OpenedCourierBoardViewModel::class.java,
        FragmentOpenedBoardBinding::inflate
    ) {

    private val commonViewModel: CourierViewModel by navGraphViewModels(R.id.courier_navigation)

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter(commonViewModel.cell?.number ?: -1)
    }

    private var isFromClick = false

    override fun initialize() {
        if (commonViewModel.cell != null) {
            Log.e("sdfsdf", "initialize: ${commonViewModel.cell!!.number.toInt()}")
            (requireActivity() as? MainActivity)?.viewModel?.openLocker(
                1,
                commonViewModel.cell!!.number.toInt()
            )
        } else {
            Log.e("sdfsdf", "initialize: ss ${-1}")
        }

        (requireActivity() as? MainActivity)?.viewModel?.observeLockerBoardEvents()
            ?.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
                        if (isFromClick && event.locker == commonViewModel.cell?.number?.toInt()) {
                            isFromClick = false
                            if (event.status == LockStatus.CLOSED) {
                                findNavController().navigate(R.id.anotherBoardDialogFragment)
                            } else {
                                findNavController().navigate(R.id.closeCellDialogFragment)
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
        binding.tvTitle.text = String.format(
            requireContext().getString(R.string.text_opened_board),
            (commonViewModel.cell?.number ?: 0).toString()
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
            if (commonViewModel.cell != null) {
                isFromClick = true
                (requireActivity() as? MainActivity)?.viewModel?.getCellStatus(
                    1,
                    commonViewModel.cell!!.number.toInt()
                )
            }
        }
        binding.btnContinue.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host).navigate(
                R.id.global_action_to_success_fragment,
                bundleOf(
                    "type" to PackageType.getInt(
                        PackageType.COURIER
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
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
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.commonViewModel.CourierViewModel
import com.example.sampleusbproject.presentation.numberPad.PackageType
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class OpenedCourierBoardFragment :
    BaseViewModelFragment<OpenedCourierBoardViewModel, FragmentOpenedBoardBinding>(
        R.layout.fragment_opened_board,
        OpenedCourierBoardViewModel::class.java,
        FragmentOpenedBoardBinding::inflate
    ) {

    @Inject
    lateinit var lockedBoard: LockerBoardInterface

    private val commonViewModel: CourierViewModel by navGraphViewModels(R.id.courier_navigation)

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter(commonViewModel.cell?.number ?: -1)
    }

    override fun initialize() {
        if (commonViewModel.cell != null){
            lockedBoard.connect()
            Log.e("sdfsdf", "initialize: ${commonViewModel.cell!!.number.toInt()}", )
            lockedBoard.openLocker(1, commonViewModel.cell!!.number.toInt())
        } else {
            Log.e("sdfsdf", "initialize: ss ${-1}", )
        }

        lifecycleScope.launch {
            lockedBoard.getEventLiveData().observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
                        Timber.tag("Flow").d("Статус Дверь: ${event.locker}, статус: ${event.status}")
                    }
                    is LockerBoardResponse.Error -> {
                        Timber.tag("Flow").e("Ошибка: ${event.message}")
                    }
                    is LockerBoardResponse.OpenDoor ->{
                        Timber.tag("Flow").d("Дверь: ${event.locker}, статус: ${event.status}")
                    }
                    else -> Timber.tag("Flow").d("Получено: $event")
                }
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
            findNavController().navigate(R.id.anotherBoardDialogFragment)
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

    override fun onDestroy() {
        super.onDestroy()
        lockedBoard.disconnect()
    }

}
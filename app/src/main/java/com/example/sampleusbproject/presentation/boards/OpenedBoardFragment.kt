package com.example.sampleusbproject.presentation.boards

import android.os.Build
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.presentation.base.BaseViewModelFragment
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.numberPad.PackageType
import com.example.sampleusbproject.presentation.numberPad.PostomatTakeCell
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class OpenedBoardFragment :
    BaseViewModelFragment<OpenedBoardViewModel, FragmentOpenedBoardBinding>(
        R.layout.fragment_opened_board,
        OpenedBoardViewModel::class.java,
        FragmentOpenedBoardBinding::inflate
    ) {

    private lateinit var adapter: BoardsAdapter

    override fun initialize() {
        val selectedCell = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("cell", PostomatTakeCell::class.java)
        } else {
            arguments?.getSerializable("cell") as? PostomatTakeCell
        }
        val selectedNumber = selectedCell?.number ?: -1L
        adapter = BoardsAdapter(selectedNumber)
        viewModel.take(selectedCell?.orderId ?: "")

            (requireActivity() as? MainActivity)?.viewModel?.observeLockerBoardEvents()?.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
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

        binding.tvTitle.text = String.format(
            requireContext().getString(R.string.text_opened_board),
            selectedNumber.toString()
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

        binding.btnBack.setOnClickListener {
            if (viewModel.successEvent.value == true) {
                if (selectedNumber > 0)
                    (requireActivity() as? MainActivity)?.viewModel?.openLocker(1, selectedNumber.toInt())
            } else
                viewModel.take(selectedCell?.orderId ?: "")
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(
                R.id.action_openedBoardFragment_to_successFragment,
                bundleOf(
                    "type" to PackageType.getInt(
                        PackageType.TAKE
                    )
                )
            )
        }
        viewModel.successEvent.observe(viewLifecycleOwner) {
            if (selectedNumber > 0)
                (requireActivity() as? MainActivity)?.viewModel?.openLocker(1, selectedNumber.toInt())
        }
    }

    override fun setupListeners() {
    }

    override fun setupSubscribers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postomat.collect {
                    adapter.submitList(it)
                }
            }
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            makeToast(R.string.text_something_went_wrong)
        }
    }

}
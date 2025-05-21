package com.example.sampleusbproject.presentation.boards

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.presentation.base.BaseFragment
import com.example.sampleusbproject.presentation.boards.adapter.Board
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.boards.adapter.BoardsModel
import com.example.sampleusbproject.presentation.numberPad.PackageType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaveParcelOpenedBoardFragment : BaseFragment<FragmentOpenedBoardBinding>(
    R.layout.fragment_opened_board,
    FragmentOpenedBoardBinding::inflate
) {

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.text = "Я оставил(-а) посылку"
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

        val data = listOf(
            BoardsModel(
                listOf(
                    Board(
                        size = BoardSize.XL,
                        number = 1,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 2,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 3,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 4,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 5,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 6,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 7,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 8,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 9,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.M,
                        number = 10,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 11,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 12,
                        usable = true
                    )
                ),
                17,
                2
            ),
            BoardsModel(
                listOf(
                    Board(
                        size = BoardSize.XL,
                        number = 13,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 14,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 15,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 16,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 17,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 18,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 19,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 20,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 21,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 22,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 23,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 24,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 25,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 26,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 27,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 28,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 29,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.S,
                        number = 30,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 31,
                        usable = true
                    ),
                    Board(
                        size = BoardSize.L,
                        number = 32,
                        usable = true
                    )
                ),
                17,
                2
            )
        )

        adapter.submitList(data)
    }

}
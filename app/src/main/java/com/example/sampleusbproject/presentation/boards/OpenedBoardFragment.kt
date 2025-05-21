package com.example.sampleusbproject.presentation.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.presentation.boards.adapter.Board
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.boards.adapter.BoardsModel
import com.example.sampleusbproject.presentation.numberPad.PackageType

class OpenedBoardFragment : Fragment(R.layout.fragment_opened_board) {

    private var _binding: FragmentOpenedBoardBinding? = null
    private val binding get() = _binding!!

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenedBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBoards.adapter = adapter
        binding.rvBoards.isNestedScrollingEnabled = false
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.successFragment, bundleOf("type" to PackageType.getInt(PackageType.TAKE)))
        }
        val dividerItemDecoration =
            BoardsDividerItemDecoration(requireContext(), 5, 5, RecyclerView.HORIZONTAL, R.drawable.board_divider)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
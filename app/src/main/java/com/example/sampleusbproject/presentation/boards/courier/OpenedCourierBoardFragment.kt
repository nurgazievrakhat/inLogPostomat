package com.example.sampleusbproject.presentation.boards.courier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentOpenedBoardBinding
import com.example.sampleusbproject.presentation.boards.adapter.CellSchema
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.boards.adapter.BoardsAdapter
import com.example.sampleusbproject.presentation.boards.adapter.BoardsDividerItemDecoration
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel

class OpenedCourierBoardFragment: Fragment(R.layout.fragment_opened_board) {

    private var _binding: FragmentOpenedBoardBinding? = null
    private val binding get() = _binding!!

    private val adapter: BoardsAdapter by lazy {
        BoardsAdapter(2)
    }

//    private val args: OpenedCourierBoardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenedBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.text = "Выбрать другую ячейку"
        val args = arguments?.getInt("type")
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.anotherBoardDialogFragment)
        }
        binding.rvBoards.adapter = adapter
        binding.rvBoards.isNestedScrollingEnabled = false
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.successFragment, bundleOf("type" to args ))
        }
        val dividerItemDecoration =
            BoardsDividerItemDecoration(requireContext(), 5, 5, RecyclerView.HORIZONTAL, R.drawable.board_divider)
        binding.rvBoards.addItemDecoration(dividerItemDecoration)

        val data = listOf(
            CellsModel(
                listOf(
                    CellSchema(
                        size = BoardSize.XL,
                        number = 1,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 2,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 3,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 4,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 5,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 6,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 7,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 8,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 9,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.M,
                        number = 10,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 11,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 12,
                        usable = true
                    )
                ),
                17,
                2
            ),
            CellsModel(
                listOf(
                    CellSchema(
                        size = BoardSize.XL,
                        number = 13,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 14,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 15,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 16,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 17,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 18,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 19,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 20,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 21,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 22,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 23,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 24,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 25,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 26,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 27,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 28,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 29,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 30,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 31,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 32,
                        usable = true
                    )
                ),
                17,
                2
            )
        )

        binding.tvTitle.setOnClickListener {
            Log.e("dsfsdfsdf", "tvTitle: ", )
            adapter.adapterMap.get(1)?.submitList(
                listOf(
                    CellSchema(
                        size = BoardSize.XL,
                        number = 13,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 14,
                        usable = false
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 15,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 16,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 17,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 18,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 19,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 20,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 21,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 22,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 23,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 24,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 25,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 26,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 27,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 28,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 29,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.S,
                        number = 30,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 31,
                        usable = true
                    ),
                    CellSchema(
                        size = BoardSize.L,
                        number = 32,
                        usable = true
                    )
                )
            )
        }

        adapter.submitList(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
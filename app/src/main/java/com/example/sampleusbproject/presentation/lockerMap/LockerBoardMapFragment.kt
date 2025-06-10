package com.example.sampleusbproject.presentation.lockerMap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.data.remote.socket.SocketRepositoryImpl
import com.example.sampleusbproject.databinding.FragmentLockerBoardMapBinding
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.domain.remote.socket.model.CellStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LockerBoardMapFragment : Fragment() {
    private var _binding: FragmentLockerBoardMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LockerBoardMapViewModel by viewModels()

    @Inject
    lateinit var postomatInfoMapper: PostomatInfoMapper

    @Inject
    lateinit var socketRepositoryImpl: SocketRepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockerBoardMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (socketRepositoryImpl.isConnected()) {
            viewModel.cellEvents.observe(viewLifecycleOwner) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val cell =
                        postomatInfoMapper.getCellNumberById(cellId = it.cellId, boardId = it.boardId)
                    if (cell != null) {
                        Timber.w("cellNumber = ${cell.first}, boardNumber = ${cell.second}")
//                    viewModel.openCell(cell.first, cell.second)
                        (requireActivity() as? MainActivity)?.viewModel?.openLocker(cell.second.toInt(),cell.first.toInt())
                        setCellStatus(cell.first, true)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось открыть ячейку",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            socketRepositoryImpl.connect()
        }

        binding.btnInfo.setOnClickListener {
            clearCellStatuses()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun setCellStatus(cellNumber: String, isOpened: Boolean) {
        if (isOpened)
            findViewByCellNumber(cellNumber)?.background =
                ResourcesCompat.getDrawable(resources, R.drawable.locker_opened, null)
        else
            findViewByCellNumber(cellNumber)?.background =
                ResourcesCompat.getDrawable(resources, R.drawable.locker_border, null)

    }

    private fun findViewByCellNumber(cellNumber: String): View? {
        for (i in 0 until binding.gridLockers.childCount) {
            val view = binding.gridLockers.getChildAt(i)
            if (view.tag == cellNumber) {
                return view
            }
        }
        return null
    }

    private fun clearCellStatuses() {
        for (i in 0 until binding.gridLockers.childCount) {
            val view = binding.gridLockers.getChildAt(i)
            view.background =
                ResourcesCompat.getDrawable(resources, R.drawable.locker_border, null)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
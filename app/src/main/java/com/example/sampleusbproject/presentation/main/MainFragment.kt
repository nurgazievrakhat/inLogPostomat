package com.example.sampleusbproject.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.BuildConfig
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.databinding.FragmentMainBinding
import com.example.sampleusbproject.presentation.numberPad.PackageType
import com.example.sampleusbproject.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var postomatInfoMapper: PostomatInfoMapper

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        viewModel.setupEventListeners()
        viewModel.postomatInfo.observe(viewLifecycleOwner) { info ->
            // Обновляем UI с данными о постомате
            Timber.e("${info.cells.size}")
        }

        viewModel.cellEvents.observe(viewLifecycleOwner) { cellData ->
            viewLifecycleOwner.lifecycleScope.launch {
                Timber.e(
                    "Open Cell -> ${
                        postomatInfoMapper.getCellNumberById(
                            cellId = cellData.cellId,
                            boardId = cellData.boardId
                        )
                    }"
                )
            }
        }

        viewModel.connectionState.observe(viewLifecycleOwner) { isConnected ->
            Timber.e(
                if (isConnected) "Соединение установлено" else "Соединение разорвано"
            )
        }
        binding.tvVersion.text = BuildConfig.VERSION_NAME

    }

    private fun setupListeners() {
        binding.btnLeave.setOnClickListener {
            checkLockerBoardAndDo {
                findNavController().navigate(R.id.leave_parcel_navigation)
            }
        }
        binding.btnTake.setOnClickListener {
            checkLockerBoardAndDo {
                findNavController().navigate(
                    R.id.enterNumberFragment,
                    bundleOf("type" to PackageType.getInt(PackageType.TAKE))
                )
            }
        }
        binding.btnCourier.setOnClickListener {
            checkLockerBoardAndDo {
                findNavController().navigate(R.id.courier_navigation)
            }
        }
    }

    private inline fun checkLockerBoardAndDo(action: () -> Unit) {
        var lockerBoardIsConnected =
            (requireActivity() as? MainActivity)?.viewModel?.isLockerBoardConnected()

        if (lockerBoardIsConnected == null || lockerBoardIsConnected == false)
            lockerBoardIsConnected =
                (requireActivity() as? MainActivity)?.viewModel?.connectLockerBoard()

        if (lockerBoardIsConnected == true)
            action()
        else
            makeToast(R.string.text_something_went_wrong)
    }
}
package com.example.sampleusbproject.presentation.main

import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.BuildConfig
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.databinding.FragmentMainBinding
import com.example.sampleusbproject.presentation.numberPad.PackageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private var usbManager: UsbManager? = null

    @Inject
    lateinit var postomatInfoMapper: PostomatInfoMapper

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                Timber.e("Open Cell -> ${postomatInfoMapper.getCellNumberById(
                    cellId = cellData.cellId, 
                    boardId = cellData.boardId
                )}")
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
            findNavController().navigate(R.id.enterNumberFragment, bundleOf("type" to PackageType.getInt(PackageType.LEAVE)))
        }
        binding.btnTake.setOnClickListener {
            findNavController().navigate(R.id.enterNumberFragment, bundleOf("type" to PackageType.getInt(PackageType.TAKE)))
        }
        binding.btnCourier.setOnClickListener {
            findNavController().navigate(R.id.enterNumberFragment, bundleOf("type" to PackageType.getInt(PackageType.COURIER)))
        }
    }
}
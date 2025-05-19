package com.example.sampleusbproject.presentation.numberPad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.databinding.FragmentEnterNumberBinding
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.lockBoards.LockBoardFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EnterNumberFragment : Fragment() {

    @Inject
    lateinit var lockedBoard: LockerBoardInterface
    private var _binding: FragmentEnterNumberBinding? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val binding get() = _binding!!
//    private val args: EnterNumberFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.keypadGrid.setOnKeyClickListener {
            binding.etCodeInput.text.append(it)
        }
//        binding.btnClear.setOnClickListener {
//            binding.etCodeInput.text.clear()
//        }
        binding.btnClear.setOnClickListener {
            if (binding.etCodeInput.text.isNotEmpty())
                binding.etCodeInput.text.delete(
                    binding.etCodeInput.text.length - 1,
                    binding.etCodeInput.text.length
                )
        }
        val args = PackageType.getType(arguments?.getInt("type") ?: 0)
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            when (code) {
                "1" -> {
                    openLock(1,1)
                }

                "10" -> {
                    openLock(1,0x10)
                }

                "11" -> {
                    openLock(1,0x10)
                }

                "16" -> {
                    openLock(1,0x16)
                }

                "30" -> {
                    openLock(1,0x30)
                }

                "5" -> {
                    openLock(1,0x05)
                }
                "25" -> {
                    lockedBoard.getLockerStatus(1,1)
                }
                "27" -> {
                    lockedBoard.getLockerStatus(1,0)
                }
                "28" -> {
                    lockedBoard.readLockTime(1)
                }
                "29" -> {
                    lockedBoard.changeLockTime(1)
                }

                "26" -> {
                    if (args == PackageType.COURIER){
                        findNavController().navigate(R.id.openedCourierBoardFragment, bundleOf("type" to PackageType.getInt(PackageType.COURIER)))
                    } else if (args == PackageType.LEAVE){
                        findNavController().navigate(R.id.receiverFragment)
                    }
                    else {
                        findNavController().navigate(R.id.openedBoardFragment)
                    }
                }

                else -> {

                }
            }
        }
        binding.btnBack.setOnClickListener {
            Timber.e("btnBack")
            findNavController().popBackStack()
        }
//        binding.buttonScan.setOnClickListener {
//            findNavController().navigate(R.id.qrFragment)
//        }
//        binding.buttonInfo.setOnClickListener {
//            findNavController().navigate(R.id.emailReceiptFragment)
//        }
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
    }

    private fun openLock(boardId:Int,lockId:Int) {
        lockedBoard.openLocker(
            boardId,
            lockId)
    }

    override fun onDestroy() {
        super.onDestroy()
        lockedBoard.disconnect()
    }
}
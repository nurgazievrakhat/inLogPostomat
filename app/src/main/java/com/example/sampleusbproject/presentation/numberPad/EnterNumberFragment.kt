package com.example.sampleusbproject.presentation.numberPad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        binding.btn0.setOnClickListener {
            binding.etCodeInput.text.append("0")
        }
        binding.btn1.setOnClickListener {
            binding.etCodeInput.text.append("1")
        }
        binding.btn2.setOnClickListener {
            binding.etCodeInput.text.append("2")
        }
        binding.btn3.setOnClickListener {
            binding.etCodeInput.text.append("3")
        }
        binding.btn4.setOnClickListener {
            binding.etCodeInput.text.append("4")
        }
        binding.btn5.setOnClickListener {
            binding.etCodeInput.text.append("5")
        }
        binding.btn6.setOnClickListener {
            binding.etCodeInput.text.append("6")
        }
        binding.btn7.setOnClickListener {
            binding.etCodeInput.text.append("7")
        }
        binding.btn8.setOnClickListener {
            binding.etCodeInput.text.append("8")
        }
        binding.btn9.setOnClickListener {
            binding.etCodeInput.text.append("9")
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
        binding.btnContinue.setOnClickListener {
            val code = binding.etCodeInput.text.toString()
            when (code) {
                "1" -> {
                    openLock(48,0x01)
                }

                "2" -> {
                    openLock(48,0x02)
                }

                "3" -> {
                    openLock(48,0x03)
                }

                "4" -> {
                    openLock(48,0x04)
                }

                "5" -> {
                    openLock(48,0x05)
                }
                "25" -> {
                    lockedBoard.getLockerStatus(0x01,0x01)
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
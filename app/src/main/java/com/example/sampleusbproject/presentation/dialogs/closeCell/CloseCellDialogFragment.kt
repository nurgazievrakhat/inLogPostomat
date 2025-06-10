package com.example.sampleusbproject.presentation.dialogs.closeCell

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.databinding.DialogCloseCellBinding
import com.example.sampleusbproject.domain.models.LockStatus
import com.example.sampleusbproject.presentation.base.NonClosableDialogFragment
import com.example.sampleusbproject.presentation.commonViewModel.CourierViewModel
import com.example.sampleusbproject.utils.makeToast
import timber.log.Timber
import kotlin.getValue

class CloseCellDialogFragment:
    NonClosableDialogFragment<DialogCloseCellBinding>(DialogCloseCellBinding::inflate) {

    private val commonViewModel: CourierViewModel by navGraphViewModels(R.id.courier_navigation)

    private var isFromClick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChange.setOnClickListener {
            isFromClick = true
            (requireActivity() as? MainActivity)?.viewModel?.getCellStatus(
                1,
                commonViewModel.cell!!.number.toInt()
            )
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        (requireActivity() as? MainActivity)?.viewModel?.observeLockerBoardEvents()
            ?.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
                        if (event.locker == commonViewModel.cell!!.number.toInt()) {
                            if (event.status == LockStatus.CLOSED) {
                                findNavController().navigate(R.id.action_closeCellDialogFragment_to_anotherBoardDialogFragment2)
                            } else if (isFromClick) {
                                makeToast(R.string.text_need_to_close_cell)
                            }
                        }
                        isFromClick = false
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
    }

}
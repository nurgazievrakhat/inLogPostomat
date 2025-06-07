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
import com.example.sampleusbproject.presentation.commonViewModel.LeaveParcelViewModel
import com.example.sampleusbproject.utils.makeToast
import timber.log.Timber

class AnotherLeaveCloseCellDialogFragment:
    NonClosableDialogFragment<DialogCloseCellBinding>(DialogCloseCellBinding::inflate) {

    private val commonViewModel: LeaveParcelViewModel by navGraphViewModels(R.id.leave_parcel_navigation)

    private var isFromClick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChange.setOnClickListener {
            if (commonViewModel.selectedCell != null){
                isFromClick = true
                (requireActivity() as? MainActivity)?.viewModel?.getCellStatus(
                    1,
                    commonViewModel.selectedCell!!.number.toInt()
                )
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        (requireActivity() as? MainActivity)?.viewModel?.observeLockerBoardEvents()
            ?.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is LockerBoardResponse.DoorStatus -> {
                        if (event.locker == commonViewModel.selectedCell?.number?.toInt()) {
                            if (event.status == LockStatus.CLOSED) {
                                findNavController().navigate(R.id.action_anotherLeaveCloseCellDialogFragment_to_anotherLeaveBoardDialogFragment)
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
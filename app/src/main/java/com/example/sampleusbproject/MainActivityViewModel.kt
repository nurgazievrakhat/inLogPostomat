package com.example.sampleusbproject

import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val lockedBoard: LockerBoardInterface
) : BaseViewModel() {

    fun isLockerBoardConnected() = lockedBoard.isConnected()

    fun connectLockerBoard() = lockedBoard.connect()

    fun observeLockerBoardEvents() = lockedBoard.getEventLiveData()

    fun openLocker(boardId: Int, cellNumber: Int) =
        lockedBoard.openLocker(boardId, cellNumber)

    fun getCellStatus(boardId: Int, cellNumber: Int) =
        lockedBoard.getLockerStatus(boardId, cellNumber)

    fun disconnectLockerBoard() = lockedBoard.disconnect()

}
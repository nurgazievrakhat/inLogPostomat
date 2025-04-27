package com.example.sampleusbproject.data

import com.example.sampleusbproject.data.reposityImpl.OpenDoorResponse
import com.example.sampleusbproject.domain.models.LockStatus

sealed class LockerBoardResponse {
    data class Raw(val json: String) : LockerBoardResponse()
    data class DoorStatus(val board: Int, val locker: Int, val status: LockStatus) : LockerBoardResponse()
    data class AllDoorsStatus(val board: Int, val binaryString: String) : LockerBoardResponse()
    data class OpenDoor(val board: Int, val locker: Int, val status: OpenDoorResponse) : LockerBoardResponse()
    data class Error(val message: String) : LockerBoardResponse()
}
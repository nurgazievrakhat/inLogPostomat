package com.example.sampleusbproject.domain.interfaces

import androidx.lifecycle.LiveData
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.domain.models.LockStatus
import kotlinx.coroutines.flow.Flow

interface LockerBoardInterface {
    fun connect(): Boolean
    fun disconnect()
    fun isConnected(): Boolean
    fun getLockerStatus(boardAddress: Int,lockerId: Int): LockStatus
    fun getAllLockersStatus(boardAddress: Int, totalLockers: Int): List<LockStatus?>?
    fun openLocker(boardAddress: Int, lockerId: Int): Boolean
    fun openLockers(boardAddress: Int, lockerIds: IntArray): Boolean
    fun getEventLiveData(): LiveData<LockerBoardResponse>
}
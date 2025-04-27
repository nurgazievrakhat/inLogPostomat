package com.example.sampleusbproject.domain.repositories

import com.example.sampleusbproject.domain.models.BoardModel
import com.example.sampleusbproject.domain.models.LockModel

interface BoardRepository {
    suspend fun diagnose(): Result<BoardModel>
    suspend fun getAllLocks(): List<LockModel>
}
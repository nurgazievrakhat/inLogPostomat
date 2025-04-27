package com.example.sampleusbproject.domain.repositories

import com.example.sampleusbproject.domain.models.LockModel

interface LockRepository {
    suspend fun unlock(lockId: Int): Result<LockModel>
    suspend fun checkStatus(lockId: Int): Result<LockModel>
}
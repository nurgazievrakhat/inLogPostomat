package com.example.sampleusbproject.domain.models

data class LockModel(
    val id: Int,
    val status: LockStatus,
    val boardAddress: Int
)

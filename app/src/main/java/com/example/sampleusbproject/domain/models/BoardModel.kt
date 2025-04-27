package com.example.sampleusbproject.domain.models

data class BoardModel(
    val address: Int,
    val locks: List<LockModel>,
    val connectionStatus: ConnectionStatus
)

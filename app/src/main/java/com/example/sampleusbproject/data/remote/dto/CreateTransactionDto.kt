package com.example.sampleusbproject.data.remote.dto

data class CreateTransactionDto(
    val amount: Long,
    val orderId: String?= null
)
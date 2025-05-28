package com.example.sampleusbproject.data.remote.dto

data class UpdateOrderCellDto(
    val orderId: String,
    val cellId: String,
    val days: Int
)
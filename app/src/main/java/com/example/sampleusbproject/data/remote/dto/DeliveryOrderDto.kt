package com.example.sampleusbproject.data.remote.dto

data class DeliveryOrderDto(
    val transactionId: String ?= null,
    val cellId: String,
    val days: Int = 2
)
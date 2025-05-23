package com.example.sampleusbproject.data.remote.dto

data class OrderDto(
    val cellId: String,
    val fromUserPhone: String,
    val toUserPhone: String,
    val days: Int,
    val transactionId: String ?= null
)
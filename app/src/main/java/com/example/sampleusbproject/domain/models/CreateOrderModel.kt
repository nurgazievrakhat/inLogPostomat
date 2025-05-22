package com.example.sampleusbproject.domain.models

import com.example.sampleusbproject.data.remote.dto.OrderDto

data class CreateOrderModel(
    val cellId: String,
    val fromUserPhone: String,
    val toUserPhone: String,
    val days: Int,
    val transactionId: String? = null
)

fun CreateOrderModel.mapToDto() = OrderDto(
    cellId = cellId,
    fromUserPhone = fromUserPhone,
    toUserPhone = toUserPhone,
    days = days,
    transactionId = transactionId
)
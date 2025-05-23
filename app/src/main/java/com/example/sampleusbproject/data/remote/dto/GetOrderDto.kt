package com.example.sampleusbproject.data.remote.dto

import com.example.sampleusbproject.domain.models.GetOrderModel
import com.example.sampleusbproject.domain.models.GetOrderType

data class GetOrderDto(
    val id: String,
    val startDateTime: String,
    val endDateTime: String,
    val isActive: Boolean,
    val days: Long,
    val currentStatusId: String,
    val companyId: String? = null,
    val fromUserId: String,
    val toUserId: String,
    val cellId: String,
    val postomatId: String,
    val rentId: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val remainder: Long
)

fun GetOrderDto.mapToDomain() = GetOrderModel(
    id = id,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    isActive = isActive,
    days = days,
    currentStatusId = currentStatusId,
    companyId = companyId,
    fromUserId = fromUserId,
    toUserId = toUserId,
    cellId = cellId,
    postomatId = postomatId,
    rentId = rentId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    remainder = remainder
)

fun GetOrderType.mapToDto() = when(this){
    GetOrderType.PICK -> "UP"
    GetOrderType.LEAVE -> "DOWN"
    GetOrderType.SEIZE -> "SUBTRACT"
}
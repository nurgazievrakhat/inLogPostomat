package com.example.sampleusbproject.domain.models

data class GetOrderModel(
    val id: String,
    val startDateTime: String ?= null,
    val endDateTime: String ?= null,
    val isActive: Boolean,
    val days: Long,
    val currentStatusId: String,
    val companyId: String ?= null,
    val fromUserId: String? = null,
    val toUserId: String? = null,
    val cellId: String ?= null,
    val postomatId: String,
    val rentId: String ?= null,
    val createdAt: String,
    val updatedAt: String,
    val remainder: Long ?= null
)
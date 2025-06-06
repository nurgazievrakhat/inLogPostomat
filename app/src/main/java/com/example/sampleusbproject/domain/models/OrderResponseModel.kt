package com.example.sampleusbproject.domain.models

data class OrderResponseModel(
    val id: String,
    val startDateTime: String,
    val endDateTime: String,
    val isActive: Boolean,
    val days: Long,
    val currentStatusId: String ?= null,
    val companyId: String ?= null,
    val fromUserId: String,
    val toUserId: String,
    val cellId: String,
    val postomatId: String,
    val rentId: String ?= null,
    val createdAt: String,
    val updatedAt: String
)
package com.example.sampleusbproject.domain.remote.socket.model

data class PostomatInfo(
    val address: String,
    val cells: List<Cell>,
    val cityId: String,
    val closeTime: String?,
    val connectionLastUpdate: String?,
    val createdAt: String,
    val id: String,
    val isActive: Boolean,
    val isConnected: Boolean,
    val lat: Double,
    val lng: Double,
    val openTime: String?,
    val title: String,
    val updatedAt: String
)
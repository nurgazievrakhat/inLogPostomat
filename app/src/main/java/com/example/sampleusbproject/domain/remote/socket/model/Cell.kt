package com.example.sampleusbproject.domain.remote.socket.model

data class Cell(
    val board: Board,
    val boardId: String,
    val createdAt: String,
    val currentOrderId: Any,
    val currentRentId: Any,
    val forRentAvailable: Boolean,
    val id: String,
    val isActive: Boolean,
    val isOpen: Boolean,
    val number: Int,
    val postomatId: String,
    val size: String,
    val title: String,
    val updatedAt: String
)
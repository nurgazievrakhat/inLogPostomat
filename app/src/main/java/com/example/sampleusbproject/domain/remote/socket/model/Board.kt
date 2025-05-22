package com.example.sampleusbproject.domain.remote.socket.model

data class Board(
    val createdAt: String,
    val id: String,
    val isActive: Boolean,
    val number: Long,
    val postomatId: String,
    val title: String,
    val updatedAt: String
)
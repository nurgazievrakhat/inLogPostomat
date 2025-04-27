package com.example.sampleusbproject.domain.remote.socket.model

data class Board(
    val createdAt: String,
    val id: String,
    val isActive: Boolean,
    val number: Int,
    val postomatId: String,
    val title: String,
    val updatedAt: String
)
package com.example.sampleusbproject.data.remote.dto

import com.example.sampleusbproject.domain.models.FreeCellModel
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

data class FreeCellDto(
    val id: String,
    val title: String,
    val number: Long,
    val size: String,
    val isActive: Boolean,
    val isOpen: Boolean,
    val forRentAvailable: Boolean,
    val boardId: String,
    val postomatId: String,
    val currentRentId: String ?= null,
    val currentOrderId: String ?= null,
    val createdAt: String,
    val updatedAt: String,
    val board: FreeBoard
)

data class FreeBoard(
    val id: String,
    val title: String,
    val number: Long,
    val isActive: Boolean,
    val postomatId: String,
    val schemaId: String,
    val createdAt: String,
    val updatedAt: String,
)

fun FreeCellDto.mapToDomain() = FreeCellModel(
    id = id,
    forRentAvailable = forRentAvailable,
    size = BoardSize.fromString(size),
    boardId = boardId,
    number = number
)
package com.example.sampleusbproject.domain.models

import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

data class CellPrice(
    val id: String,
    val price: Long,
    val size: BoardSize
)
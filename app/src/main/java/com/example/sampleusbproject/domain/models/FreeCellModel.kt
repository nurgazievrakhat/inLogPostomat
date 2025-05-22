package com.example.sampleusbproject.domain.models

import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

class FreeCellModel(
    val id: String,
    val forRentAvailable: Boolean,
    val size: BoardSize,
    val boardId: String,
    val number: Long
)
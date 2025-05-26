package com.example.sampleusbproject.presentation.commonViewModel

import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

data class SelectedCell(
    val id: String,
    val number: Long,
    val size: BoardSize
)
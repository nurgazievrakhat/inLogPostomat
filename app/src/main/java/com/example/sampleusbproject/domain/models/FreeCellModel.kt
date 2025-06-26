package com.example.sampleusbproject.domain.models

import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.cell.groupFreeCellBySize
import com.example.sampleusbproject.usecases.FreeCellWithAmount

class FreeCellModel(
    val id: String,
    val forRentAvailable: Boolean,
    val size: BoardSize,
    val boardId: String,
    val number: Long
)

fun List<FreeCellModel>.mapWithAmount(prices: List<CellPrice>) = this.groupFreeCellBySize().map { freeCell ->
    val sizeAmount = prices.find { it.size == freeCell.value.size }

    checkNotNull(sizeAmount)

    FreeCellWithAmount(
        freeCell.value.id,
        freeCell.value.forRentAvailable,
        freeCell.value.size,
        freeCell.value.boardId,
        freeCell.value.number,
        sizeAmount.price
    )
}
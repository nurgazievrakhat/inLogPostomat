package com.example.sampleusbproject.presentation.cell

import com.example.sampleusbproject.domain.models.FreeCellModel
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.usecases.FreeCellWithAmount

data class SelectCellModel(
    val cellId: String? = null,
    val number: Long? = null,
    var isSelected: Boolean = false,
    val boardSize: BoardSize,
    val cellPrice: Long,
    val isAvailableToChoose: Boolean
) {

    fun hasFreeCells() = cellId != null

}

fun List<FreeCellModel>.groupFreeCellBySize() = this
    .filter { it.forRentAvailable }
    .groupBy { it.size }
    .mapValues {
        it.value[0]
    }

// we need to select free cell, that's why we group them by size and we take first free cell for this size,
// because user can only select size, not cell
fun List<FreeCellWithAmount>.mapToUi(previousSelectedSize: BoardSize? = null): List<SelectCellModel> {
    return BoardSize.entries.mapNotNull { size ->
        val model = this.find { it.size == size }
        if (size.size >= (previousSelectedSize?.size ?: 0) && model != null)
            SelectCellModel(
                cellId = model.id,
                number = model.number,
                isSelected = false,
                boardSize = size,
                isAvailableToChoose = true,
                cellPrice = model.amount
            )
        else null
    }
}
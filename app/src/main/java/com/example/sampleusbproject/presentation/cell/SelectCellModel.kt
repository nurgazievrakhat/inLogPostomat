package com.example.sampleusbproject.presentation.cell

import com.example.sampleusbproject.domain.models.FreeCellModel
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

data class SelectCellModel(
    val cellId: String ?= null,
    val number: Long ?= null,
    var isSelected: Boolean = false,
    val boardSize: BoardSize
) {

    fun isAvailableToChoose() = cellId != null

}

// we need to select free cell, that's why we group them by size and we take first free cell for this size,
// because user can only select size, not cell
fun List<FreeCellModel>.mapToUi(): List<SelectCellModel> {
    val filteredList = this
        .filter { it.forRentAvailable }
        .groupBy { it.size }

    val newList = filteredList.mapValues {
        it.value[0]
    }

    return BoardSize.entries.map {
        val model = newList[it]
        SelectCellModel(
            cellId = model?.id,
            number = model?.number,
            isSelected = false,
            boardSize = it
        )
    }
}
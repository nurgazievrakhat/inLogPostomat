package com.example.sampleusbproject.data.remote.dto

import com.example.sampleusbproject.domain.models.CellPrice
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize

data class CellTariffDto(
    val id: String,
    val type: String,
    val prices: List<CellPriceDto>
)

data class CellPriceDto(
    val id: String,
    val price: Long,
    val size: String,
    val tariffId: String
)

fun CellPriceDto.toDomain() = CellPrice(
    id = id,
    price = price,
    size = BoardSize.fromString(size)
)

fun List<CellPriceDto>.toDomain() = this.map {
    it.toDomain()
}
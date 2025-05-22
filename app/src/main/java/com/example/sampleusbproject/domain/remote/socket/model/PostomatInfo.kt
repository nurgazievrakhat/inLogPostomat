package com.example.sampleusbproject.domain.remote.socket.model

import com.example.sampleusbproject.data.local.entity.BoardsModel
import com.example.sampleusbproject.presentation.boards.adapter.CellSchema
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class PostomatInfo(
    val address: String,
    val cells: List<Cell>,
    val cityId: String,
    val closeTime: String?,
    val connectionLastUpdate: String?,
    val createdAt: String,
    val id: String,
    val isActive: Boolean,
    val isConnected: Boolean,
    val lat: Double,
    val lng: Double,
    val openTime: String?,
    val title: String,
    val updatedAt: String,
    val boards: List<BoardsDto>
)

data class BoardsDto(
    val id: String,
    val title: String,
    val number: Long,
    val isActive: Boolean,
    val postomatId: String,
    val schemaId: String,
    val createdAt: String,
    val updatedAt: String,
    val cells: List<CellDto2>,
    val schema: SchemaDto
)

data class SchemaDto(
    val id: String,
    val title: String,
    val schema: List<List<CellsDto>>
)

data class CellDto(
    val size: String,
    val number: Long,
    val usable: Boolean
)

data class CellDto2(
    val id: String,
    val title: String,
    val number: Long,
    val size: String,
    val isActive: Boolean,
    val isOpen: Boolean,
    val forRentAvailable: Boolean,
    val boardId: String,
    val postomatId: String,
    val currentRentId: Any?,
    val currentOrderId: String?,
    val createdAt: String,
    val updatedAt: String,
)

sealed class CellsDto {
    data class Single(
        val size: String,
        val number: Long,
        val usable: Boolean
    ) : CellsDto()

    data class Multiple(
        val cells: List<CellDto>
    ) : CellsDto()
}

class ResponseDeserializer : JsonDeserializer<CellsDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        ctx: JsonDeserializationContext
    ): CellsDto {
        val obj = json.asJsonObject
        return if (obj.has("cells")) {
            val cells =
                ctx.deserialize<Array<CellDto>>(obj.get("cells"), Array<CellDto>::class.java)
                    .toList()
            CellsDto.Multiple(cells.toList())
        } else {
            val size = obj.get("size").asString
            val number = obj.get("number").asLong
            val usable = obj.get("usable").asBoolean
            CellsDto.Single(size, number, usable)
        }
    }
}

fun CellDto.mapToUi() = CellSchema(
    size = BoardSize.fromString(size),
    number = number,
    usable = usable
)

fun CellsDto.Single.mapToUi() = CellSchema(
    size = BoardSize.fromString(size),
    number = number,
    usable = usable
)

fun List<CellsDto>.mapToSchemaUi(): List<CellSchema> {
    val newList = mutableListOf<CellSchema>()
    this.forEach { cell ->
        when (cell) {
            is CellsDto.Single -> newList.add(cell.mapToUi())
            is CellsDto.Multiple -> cell.cells.forEach { includedCell ->
                newList.add(includedCell.mapToUi())
            }
        }
    }
    return newList.toList()
}

fun List<List<CellsDto>>.mapToUi() = map {
    val list = it.mapToSchemaUi()
    var columnItemCount = 0.0
    list.forEach {
        if (it.size.ratioWidth == 1) // means that in one column will contains two items
            columnItemCount += 0.5
        else
            columnItemCount += it.size.ratioHeight
    }
    CellsModel(
        list = list,
        columnItemCount = columnItemCount.toInt(),
        rawItemCount = 2 // rawItemCount is always 2
    )
}

fun List<BoardsDto>.mapToBoardsModel() = map {
    it.mapToUi()
}

fun BoardsDto.mapToUi() = BoardsModel(
    id = id,
    title,
    number,
    isActive,
    postomatId,
    schemaId,
    createdAt,
    updatedAt,
    schema = schema.schema.mapToUi()
)

//fun SchemaDto.mapToUi(): List<BoardsModel> = schema.map { cells ->
//    BoardsModel(
//        list = cells.mapToUi(),
//        columnItemCount = cells.size,
//        rawItemCount = 2 // rawItemCount is always 2
//    )
//}

package com.example.sampleusbproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sampleusbproject.data.local.converter.AnyTypeConverter
import com.example.sampleusbproject.data.local.converter.BoardsListModelConverter
import com.example.sampleusbproject.data.local.converter.BoardsModelConverter
import com.example.sampleusbproject.data.local.converter.CellListConverter
import com.example.sampleusbproject.domain.remote.socket.model.Cell
import com.example.sampleusbproject.domain.remote.socket.model.CellDto2
import com.example.sampleusbproject.domain.remote.socket.model.SchemaDto
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel

@Entity(tableName = "postomat_info")
@TypeConverters(CellListConverter::class, BoardsModelConverter::class, BoardsListModelConverter::class, AnyTypeConverter::class)
data class PostomatInfoEntity(
    @PrimaryKey
    val id: String,
    val cells: List<Cell>,
    val address: String,
    val cityId: String,
    val closeTime: String?,
    val connectionLastUpdate: String?,
    val createdAt: String,
    val isActive: Boolean,
    val isConnected: Boolean,
    val lat: Double,
    val lng: Double,
    val openTime: String?,
    val title: String,
    val updatedAt: String,
    val boards: List<BoardsModel>
)

data class BoardsModel(
    val id: String,
    val title: String,
    val number: Long,
    val isActive: Boolean,
    val postomatId: String,
    val schemaId: String,
    val createdAt: String,
    val updatedAt: String,
    val schema: List<CellsModel>
)

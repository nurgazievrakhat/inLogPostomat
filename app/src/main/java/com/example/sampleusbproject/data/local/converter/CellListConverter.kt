package com.example.sampleusbproject.data.local.converter

import androidx.room.TypeConverter
import com.example.sampleusbproject.data.local.entity.BoardsModel
import com.example.sampleusbproject.domain.remote.socket.model.Cell
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CellListConverter {
    private val gson = Gson()

    private val type = object : TypeToken<List<Cell>>() {}.type

    @TypeConverter
    fun fromString(value: String): List<Cell> {
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<Cell>): String {
        return gson.toJson(list)
    }
}

class BoardsListModelConverter {
    private val gson = Gson()

    private val type = object : TypeToken<List<CellsModel>>() {}.type

    @TypeConverter
    fun fromString(value: String): List<CellsModel> {
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<CellsModel>): String {
        return gson.toJson(list)
    }
}

class BoardsModelConverter {
    private val gson = Gson()

    private val type = object : TypeToken<List<BoardsModel>>() {}.type

    @TypeConverter
    fun fromString(value: String): List<BoardsModel> {
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<BoardsModel>): String {
        return gson.toJson(list)
    }
}
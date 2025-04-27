package com.example.sampleusbproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sampleusbproject.data.local.entity.PostomatInfoEntity
import com.example.sampleusbproject.data.local.converter.CellListConverter

@Dao
interface PostomatInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostomatInfo(postomatInfo: PostomatInfoEntity)

    @Query("SELECT * FROM postomat_info WHERE id = :id")
    suspend fun getPostomatInfo(id: String): PostomatInfoEntity?

    @Query("DELETE FROM postomat_info WHERE id = :id")
    suspend fun deletePostomatInfo(id: String)

    @Query("SELECT cells FROM postomat_info")
    suspend fun getCellsByBoardId(): String?

    suspend fun getCellNumber(boardId: String, cellId: String): Int? {
        val cellsJson = getCellsByBoardId() ?: return null
        val cells = CellListConverter().fromString(cellsJson)
        return cells.find { it.id == cellId && it.boardId == boardId }?.number
    }
} 
package com.example.sampleusbproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sampleusbproject.data.local.dao.PostomatInfoDao
import com.example.sampleusbproject.data.local.entity.PostomatInfoEntity

@Database(
    entities = [PostomatInfoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postomatInfoDao(): PostomatInfoDao
} 
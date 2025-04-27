package com.example.sampleusbproject.data

import com.example.sampleusbproject.data.local.dao.PostomatInfoDao
import com.example.sampleusbproject.data.local.entity.PostomatInfoEntity
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import javax.inject.Inject

class PostomatInfoMapper @Inject constructor(
    private val postomatInfoDao: PostomatInfoDao
) {
    suspend fun savePostomatInfo(postomatInfo: PostomatInfo) {
        val entity = PostomatInfoEntity(
            id = postomatInfo.id,
            cells = postomatInfo.cells,
            address = postomatInfo.address,
            cityId = postomatInfo.cityId,
            closeTime = postomatInfo.closeTime,
            connectionLastUpdate = postomatInfo.connectionLastUpdate,
            createdAt = postomatInfo.createdAt,
            isActive = postomatInfo.isActive,
            isConnected = postomatInfo.isConnected,
            lat = postomatInfo.lat,
            lng = postomatInfo.lng,
            openTime = postomatInfo.openTime,
            title = postomatInfo.title,
            updatedAt = postomatInfo.updatedAt
        )
        postomatInfoDao.insertPostomatInfo(entity)
    }

    suspend fun getPostomatInfo(id: String): PostomatInfo? {
        return postomatInfoDao.getPostomatInfo(id)?.let { entity ->
            PostomatInfo(
                id = entity.id,
                cells = entity.cells,
                address = entity.address,
                cityId = entity.cityId,
                closeTime = entity.closeTime,
                connectionLastUpdate = entity.connectionLastUpdate,
                createdAt = entity.createdAt,
                isActive = entity.isActive,
                isConnected = entity.isConnected,
                lat = entity.lat,
                lng = entity.lng,
                openTime = entity.openTime,
                title = entity.title,
                updatedAt = entity.updatedAt
            )
        }
    }

    suspend fun getCellNumberById(boardId: String, cellId: String): Int? {
        return postomatInfoDao.getCellNumber(boardId, cellId)
    }
}
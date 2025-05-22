package com.example.sampleusbproject.data

import com.example.sampleusbproject.data.local.dao.PostomatInfoDao
import com.example.sampleusbproject.data.local.entity.PostomatInfoEntity
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import com.example.sampleusbproject.domain.remote.socket.model.mapToBoardsModel
import com.example.sampleusbproject.domain.remote.socket.model.mapToUi
import kotlinx.coroutines.flow.Flow
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
            updatedAt = postomatInfo.updatedAt,
            boards = postomatInfo.boards.mapToBoardsModel()
        )
        postomatInfoDao.insertPostomatInfo(entity)
    }

    suspend fun getPostomatInfo(id: String): PostomatInfoEntity? {
        return postomatInfoDao.getPostomatInfo(id)
    }

    fun observePostomatInfo(): Flow<List<PostomatInfoEntity>> {
        return postomatInfoDao.observePostomatInfo()
    }

    suspend fun getCellNumberById(boardId: String, cellId: String): Pair<String,String>? {
        return postomatInfoDao.getCellNumber(boardId, cellId)
    }


}
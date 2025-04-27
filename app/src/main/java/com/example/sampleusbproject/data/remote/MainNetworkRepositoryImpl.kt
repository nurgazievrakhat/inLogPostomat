package com.example.sampleusbproject.data.remote

import android.util.Log
import com.example.sampleusbproject.data.remote.dto.TokenRequest
import com.example.sampleusbproject.domain.remote.MainNetworkRepository
import com.example.sampleusbproject.utils.CommonPrefs
import retrofit2.http.Body
import timber.log.Timber
import javax.inject.Inject

class MainNetworkRepositoryImpl @Inject constructor(
    private val baseService: BaseService,
    private val commonPrefs: CommonPrefs
) : MainNetworkRepository {

    override suspend fun getAccessToken(phone: String,password: String) : Boolean {
        val response = baseService.getPostomatToken(TokenRequest(phone,password))
        if (response.isSuccessful && response.body() != null) {
            commonPrefs.saveAccessToken(response.body()!!.token)
        }
        return response.isSuccessful
    }
}
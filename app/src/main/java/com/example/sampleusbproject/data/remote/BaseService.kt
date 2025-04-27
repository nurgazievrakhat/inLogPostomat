package com.example.sampleusbproject.data.remote

import com.example.sampleusbproject.data.remote.dto.TokenRequest
import com.example.sampleusbproject.data.remote.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BaseService {
    @POST("api/v1/postomat/get-token")
    suspend fun getPostomatToken(@Body request: TokenRequest): Response<TokenResponse>
}
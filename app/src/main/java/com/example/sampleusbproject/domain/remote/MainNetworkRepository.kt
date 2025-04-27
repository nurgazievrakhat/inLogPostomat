package com.example.sampleusbproject.domain.remote

interface MainNetworkRepository {

    suspend fun getAccessToken(phone: String,password: String) : Boolean
}
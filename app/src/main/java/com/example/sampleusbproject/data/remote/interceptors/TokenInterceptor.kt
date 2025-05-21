package com.example.sampleusbproject.data.remote.interceptors

import android.util.Log
import com.example.sampleusbproject.utils.CommonPrefs
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val prefs: CommonPrefs
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldAccessToken = prefs.getAccessToken() ?: ""
        Log.e("retrying", "intercept")
        return chain.proceed(newRequestWithAccessToken(chain.request(), oldAccessToken))
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}
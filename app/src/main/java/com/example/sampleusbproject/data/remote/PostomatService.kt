package com.example.sampleusbproject.data.remote

import com.example.sampleusbproject.data.remote.dto.ConfirmPhoneDto
import com.example.sampleusbproject.data.remote.dto.FreeCellDto
import com.example.sampleusbproject.data.remote.dto.GetOrderDto
import com.example.sampleusbproject.data.remote.dto.OrderDto
import com.example.sampleusbproject.data.remote.dto.OrderResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostomatService {

    @GET("/api/v1/auth/send-confirm-code")
    suspend fun getSmsConfirmCode(
        @Query("phone") phone: String
    ): Response<Unit>

    @POST("/api/v1/postomat/user/confirm-user")
    suspend fun confirmPhone(
        @Body model: ConfirmPhoneDto
    ): Response<Boolean>

    @POST("/api/v1/postomat/order/{id}/take")
    suspend fun takeOrder(
        @Path("id") orderId: String
    ): Response<Unit>

    @GET("/api/v1/postomat/free-cells")
    suspend fun getFreCells(): Response<List<FreeCellDto>>

    @POST("/api/v1/postomat/order/create")
    suspend fun createOrder(
        @Body order: OrderDto
    ): Response<OrderResponseDto>

    @GET("/api/v1/postomat/order/by-password")
    suspend fun getOrderByPassword(
        @Query("type") type: String,
        @Query("password") password: String
    ): Response<GetOrderDto>

}
package com.example.sampleusbproject.domain.remote

import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.CreateOrderModel
import com.example.sampleusbproject.domain.models.FreeCellModel
import com.example.sampleusbproject.domain.models.GetOrderError
import com.example.sampleusbproject.domain.models.GetOrderModel
import com.example.sampleusbproject.domain.models.GetOrderType
import com.example.sampleusbproject.domain.models.OrderResponseModel

interface PostomatRepository {

    suspend fun getSmsCode(phone: String): Either<Unit, Unit>

    suspend fun confirmPhone(phone: String, confirmCode: String): Either<Unit, Boolean>

    suspend fun getFreeCells(): Either<Unit, List<FreeCellModel>>

    suspend fun createOrder(order: CreateOrderModel): Either<Unit, OrderResponseModel>

    suspend fun updateCell(orderId: String, cellId: String, days: Int): Either<Unit, Unit>

    suspend fun createTransaction(amount: Long, orderId: String): Either<Unit, Unit>

    suspend fun take(orderId: String): Either<Unit, Unit>

    suspend fun getOrderByPassword(type: GetOrderType, password: String): Either<GetOrderError, GetOrderModel>

    suspend fun delivery(orderId: String, cellId: String): Either<Unit, Unit>

}
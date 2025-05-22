package com.example.sampleusbproject.domain.remote

import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.CreateOrderModel
import com.example.sampleusbproject.domain.models.FreeCellModel

interface PostomatRepository {

    suspend fun getSmsCode(phone: String): Either<Unit, Unit>

    suspend fun confirmPhone(phone: String, confirmCode: String): Either<Unit, Boolean>

    suspend fun getFreeCells(): Either<Unit, List<FreeCellModel>>

    suspend fun createOrder(order: CreateOrderModel): Either<Unit, Unit>

}
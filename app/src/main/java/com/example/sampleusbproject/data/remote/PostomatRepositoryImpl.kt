package com.example.sampleusbproject.data.remote

import com.example.sampleusbproject.data.remote.dto.ConfirmPhoneDto
import com.example.sampleusbproject.data.remote.dto.mapToDomain
import com.example.sampleusbproject.domain.models.CreateOrderModel
import com.example.sampleusbproject.domain.models.FreeCellModel
import com.example.sampleusbproject.domain.models.mapToDto
import com.example.sampleusbproject.domain.remote.PostomatRepository
import javax.inject.Inject

class PostomatRepositoryImpl @Inject constructor(
    private val service: PostomatService
) : PostomatRepository {

    override suspend fun getSmsCode(phone: String): Either<Unit, Unit> {
        return try {
            val response = service.getSmsConfirmCode(phone)
            if (response.isSuccessful)
                Either.Right(Unit)
            else
                Either.Left(Unit)
        } catch (e: Exception) {
            Either.Left(Unit)
        }
    }

    override suspend fun confirmPhone(phone: String, confirmCode: String): Either<Unit, Boolean> {
        return try {
            val response = service.confirmPhone(
                ConfirmPhoneDto(
                    phone, confirmCode
                )
            )
            if (response.isSuccessful && response.body() != null)
                Either.Right(response.body()!!)
            else if (response.code() == 400)
                Either.Right(false)
            else
                Either.Left(Unit)
        } catch (e: Exception) {
            Either.Left(Unit)
        }
    }

    override suspend fun getFreeCells(): Either<Unit, List<FreeCellModel>> {
        return try {
            val response = service.getFreCells()
            if (response.isSuccessful && response.body() != null)
                Either.Right(response.body()!!.map {
                    it.mapToDomain()
                })
            else
                Either.Left(Unit)
        } catch (e: Exception) {
            Either.Left(Unit)
        }
    }

    override suspend fun createOrder(order: CreateOrderModel): Either<Unit, Unit> {
        return try {
            val response = service.createOrder(order.mapToDto())
            if (response.isSuccessful && response.body() != null)
                Either.Right(Unit)
            else
                Either.Left(Unit)
        } catch (e: Exception) {
            Either.Left(Unit)
        }
    }

}
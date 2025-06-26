package com.example.sampleusbproject.usecases

import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.mapWithAmount
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFreeCellWithAmountUseCase @Inject constructor(
    private val postomatRepository: PostomatRepository
) {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val freeCellsAsync = async { postomatRepository.getFreeCells() }
        val cellsPriceAsync = async { postomatRepository.getCellPrices() }
        val freeCellsResponse = freeCellsAsync.await()
        val cellsPriceResponse = cellsPriceAsync.await()

        return@withContext when {
            freeCellsResponse is Either.Right && cellsPriceResponse is Either.Right -> {
                val mappedData = freeCellsResponse.value.mapWithAmount(cellsPriceResponse.value)
                Either.Right(
                    mappedData
                )
            }

            else -> Either.Left(Unit)
        }
    }

}

data class FreeCellWithAmount(
    val id: String,
    val forRentAvailable: Boolean,
    val size: BoardSize,
    val boardId: String,
    val number: Long,
    val amount: Long
)
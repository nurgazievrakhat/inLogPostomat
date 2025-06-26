package com.example.sampleusbproject.presentation.cell

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.CreateOrderModel
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.usecases.GetFreeCellWithAmountUseCase
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCellViewModel @Inject constructor(
    private val postomatRepository: PostomatRepository,
    private val getFreeCellWithAmountUseCase: GetFreeCellWithAmountUseCase
) : BaseViewModel() {

    private val _freeCells = MutableStateFlow<List<SelectCellModel>>(listOf())
    val freeCells = _freeCells.asStateFlow()

    val createSuccessEvent = SingleLiveEvent<String>()

    val updateSuccessEvent = SingleLiveEvent<Boolean>()

    val errorEvent = SingleLiveEvent<Boolean>()

    fun getFreCells(previousSelectedSize: BoardSize?= null) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = getFreeCellWithAmountUseCase.invoke()
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> _freeCells.value = response.value.mapToUi(previousSelectedSize)
            }
        }
    }

    fun createOrder(
        cellId: String ,
        fromUserPhone: String,
        toUserPhone: String,
        days: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.createOrder(
                CreateOrderModel(
                    cellId = cellId,
                    fromUserPhone = fromUserPhone,
                    toUserPhone = toUserPhone,
                    days = days
                )
            )
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> createSuccessEvent.postValue(response.value.id)
            }
        }
    }

    fun updateCell(
        cellId: String,
        orderId: String,
        days: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.updateCell(
                orderId = orderId,
                cellId = cellId,
                days
            )
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> updateSuccessEvent.postValue(true)
            }
        }
    }

}
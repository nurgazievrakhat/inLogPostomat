package com.example.sampleusbproject.presentation.cell.courier

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.CreateOrderModel
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.presentation.boards.adapter.BoardSize
import com.example.sampleusbproject.presentation.cell.SelectCellModel
import com.example.sampleusbproject.presentation.cell.mapToUi
import com.example.sampleusbproject.presentation.commonViewModel.SelectedCell
import com.example.sampleusbproject.usecases.GetFreeCellWithAmountUseCase
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourierSelectCellViewModel @Inject constructor(
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

    fun deliveryOrder(
        orderId: String,
        cellId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.delivery(
                orderId = orderId, cellId = cellId
            )
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> createSuccessEvent.postValue(orderId)
            }
        }
    }

    fun updateCell(
        cellId: String ,
        orderId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.updateCell(
                orderId = orderId,
                cellId = cellId,
                days = 2
            )
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> updateSuccessEvent.postValue(true)
            }
        }
    }

}
package com.example.sampleusbproject.presentation.numberPad

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.GetOrderError
import com.example.sampleusbproject.domain.models.GetOrderType
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterNumberViewModel @Inject constructor(
    private val postomatRepository: PostomatRepository,
    private val postomatSocketUseCase: PostomatSocketUseCase
) : BaseViewModel() {

    val successEvent = SingleLiveEvent<PostomatTakeCell>()
    val paymentEvent = SingleLiveEvent<Pair<Long, PostomatTakeCell>>()
    val errorEvent = SingleLiveEvent<GetOrderError>()

    fun payed(model: PostomatTakeCell, remainder: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = postomatRepository.createTransaction(
                remainder,
                model.id
            )
            when (response) {
                is Either.Right -> successEvent.postValue(model)
                is Either.Left -> errorEvent.postValue(GetOrderError.Unexpected)
            }
        }
    }

    fun getOrderByPassword(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.getOrderByPassword(GetOrderType.PICK, password)
            when {
                response is Either.Right && response.value.cellId != null -> {
                    val postomatCell =
                        postomatSocketUseCase.getPostomatCellById(response.value.cellId)
                    val remainder = (response.value.remainder ?: 0)
                    val isPayed = remainder > 0
                    _alertLiveData.postValue(false)
                    if (postomatCell != null) {
                        val successModel = PostomatTakeCell(
                            response.value.id,
                            response.value.cellId,
                            postomatCell.number,
                            postomatCell.boardId
                        )
                        if (!isPayed)
                            successEvent.postValue(
                                successModel
                            )
                        else
                            paymentEvent.postValue(
                                Pair(
                                    remainder, successModel
                                )
                            )
                    } else
                        errorEvent.postValue(GetOrderError.NotFound)
                }

                response is Either.Left -> {
                    _alertLiveData.postValue(false)
                    errorEvent.postValue(response.value)
                }
            }
        }
    }

}
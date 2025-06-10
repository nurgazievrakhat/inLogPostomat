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
    val errorEvent = SingleLiveEvent<GetOrderError>()

    fun getOrderByPassword(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.getOrderByPassword(GetOrderType.PICK, password)
            _alertLiveData.postValue(false)
            when {
                response is Either.Right && response.value.cellId != null -> {
                    val postomatCell =
                        postomatSocketUseCase.getPostomatCellById(response.value.cellId)
                    val remainder = (response.value.remainder ?: 0)
                    val isPayed = if (remainder > 0)
                        postomatRepository.createTransaction(
                            remainder,
                            response.value.id
                        ) is Either.Right
                    else
                        true
                    if (postomatCell != null && isPayed)
                        successEvent.postValue(
                            PostomatTakeCell(
                                response.value.id,
                                response.value.cellId,
                                postomatCell.number,
                                postomatCell.boardId
                            )
                        )
                    else if (!isPayed)
                        errorEvent.postValue(GetOrderError.Unexpected)
                    else
                        errorEvent.postValue(GetOrderError.NotFound)
                }

                response is Either.Left -> errorEvent.postValue(response.value)
            }
        }
    }

}
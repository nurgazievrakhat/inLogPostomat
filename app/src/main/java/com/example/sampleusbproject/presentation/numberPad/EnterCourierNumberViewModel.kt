package com.example.sampleusbproject.presentation.numberPad

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.models.GetOrderError
import com.example.sampleusbproject.domain.models.GetOrderModel
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
class EnterCourierNumberViewModel @Inject constructor(
    private val postomatRepository: PostomatRepository,
    private val postomatSocketUseCase: PostomatSocketUseCase
) : BaseViewModel() {

    val successEvent = SingleLiveEvent<GetOrderModel>()
    val errorEvent = SingleLiveEvent<GetOrderError>()

    fun getOrderByPassword(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.getOrderByPassword(GetOrderType.DELIVERY, password)
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Right -> {
                    successEvent.postValue(
                        response.value
                    )
                }

                is Either.Left -> {
                    errorEvent.postValue(response.value)
                }
            }
        }
    }

}
package com.example.sampleusbproject.presentation.smsCode

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterSmsCodeViewModel @Inject constructor(
    private val postomatRepository: PostomatRepository
) : BaseViewModel() {

    val onSuccessEvent = SingleLiveEvent<Boolean>()

    val errorEvent = SingleLiveEvent<Boolean>()

    fun sendSmsCode(phone: String, smsCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.confirmPhone(phone, smsCode)
            _alertLiveData.postValue(false)
            when (response) {
                is Either.Left -> errorEvent.postValue(false)
                is Either.Right -> {
                    if (response.value)
                        onSuccessEvent.postValue(true)
                    else
                        errorEvent.postValue(true)
                }
            }
        }
    }

}
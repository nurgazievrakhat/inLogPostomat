package com.example.sampleusbproject.presentation.phoneNumber

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
class EnterPhoneNumberViewModel@Inject constructor(
    private val postomatRepository: PostomatRepository
): BaseViewModel() {

    val onSuccessEvent = SingleLiveEvent<Boolean>()

    val errorEvent = SingleLiveEvent<Boolean>()

    fun sendSmsCode(phone: String){
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.getSmsCode(phone)
            _alertLiveData.postValue(false)
            when(response){
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> onSuccessEvent.postValue(true)
            }
        }
    }

}
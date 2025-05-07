package com.example.sampleusbproject.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.domain.remote.MainNetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val mainNetworkRepository: MainNetworkRepository
    ) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = mainNetworkRepository.getAccessToken(phone, password)
                if (result) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Ошибка авторизации")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Ошибка авторизации")
            }
        }
    }
} 
package com.example.sampleusbproject.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleusbproject.domain.remote.MainNetworkRepository
import com.example.sampleusbproject.domain.remote.socket.model.CellData
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainNetworkRepository: MainNetworkRepository,
    private val postomatUseCase: PostomatSocketUseCase
) : ViewModel() {

    suspend fun getAccessToken(phone: String, password:String) =
        mainNetworkRepository.getAccessToken(phone, password)

    //region socket
    private val _postomatInfo = MutableLiveData<PostomatInfo>()
    val postomatInfo: LiveData<PostomatInfo> = _postomatInfo

    private val _cellEvents = MutableLiveData<CellData>()
    val cellEvents: LiveData<CellData> = _cellEvents

    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> = _connectionState

    fun connectToServer() = postomatUseCase.connectToPostomatServer()

    fun disconnectFromServer() {
        postomatUseCase.disconnectFromPostomatServer()
        _connectionState.value = false
    }

    fun requestPostomatInfo() {
        postomatUseCase.requestPostomatInfo()
    }

    fun requestPostomatStatus() {
        postomatUseCase.requestPostomatStatus()
    }

    fun updateCellStatus(cellId: String, isOpened: Boolean) {
        postomatUseCase.updateCellStatus(cellId, isOpened)
    }

    fun sendCellCommand(cellId: String, boardId: String, isOpened: Boolean) {
        postomatUseCase.sendCellCommand(cellId, boardId, isOpened)
    }

    fun setupEventListeners() {
        postomatUseCase.observePostomatInfo { info ->
            _postomatInfo.value = info
        }

        postomatUseCase.observePostomatCellEvents { cellData ->
            _cellEvents.value = cellData
        }

        postomatUseCase.observePostomatUpdates {
            if (postomatUseCase.isConnected()) requestPostomatInfo()
        }
    }

    override fun onCleared() {
        disconnectFromServer()
        super.onCleared()
    }
}
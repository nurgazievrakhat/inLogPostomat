package com.example.sampleusbproject.presentation.boards

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenedBoardViewModel@Inject constructor(
    private val postomatUseCase: PostomatSocketUseCase,
    private val postomatRepository: PostomatRepository
) : BaseViewModel() {

    private val _postomatCells = MutableStateFlow<List<CellsModel>>(listOf())
    val postomat = _postomatCells.asStateFlow()

    val successEvent = SingleLiveEvent<Boolean>()

    val errorEvent = SingleLiveEvent<Boolean>()

    init {
        getPostomat()
    }

    private fun getPostomat() {
        viewModelScope.launch(Dispatchers.IO) {
            postomatUseCase.observePostamatCellLocal().collect {
                if (it.isNotEmpty())
                    if (it[0].boards.isNotEmpty())
                        _postomatCells.value = it[0].boards[0].schema
            }
        }
    }

    fun take(orderId: String){
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.take(orderId)
            _alertLiveData.postValue(false)
            when(response){
                is Either.Right -> successEvent.postValue(true)
                is Either.Left -> errorEvent.postValue(true)
            }
        }
    }

}
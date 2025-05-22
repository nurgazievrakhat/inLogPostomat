package com.example.sampleusbproject.presentation.cell

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.data.remote.Either
import com.example.sampleusbproject.domain.remote.PostomatRepository
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCellViewModel@Inject constructor(
    private val postomatRepository: PostomatRepository
): BaseViewModel() {

    private val _freeCells = MutableStateFlow<List<SelectCellModel>>(listOf())
    val freeCells = _freeCells.asStateFlow()

    val errorEvent = SingleLiveEvent<Boolean>()

    fun getFreCells(){
        viewModelScope.launch(Dispatchers.IO) {
            _alertLiveData.postValue(true)
            val response = postomatRepository.getFreeCells()
            _alertLiveData.postValue(false)
            when(response){
                is Either.Left -> errorEvent.postValue(true)
                is Either.Right -> _freeCells.value = response.value.mapToUi()
            }
        }
    }

}
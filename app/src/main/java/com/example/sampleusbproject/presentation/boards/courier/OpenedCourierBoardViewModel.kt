package com.example.sampleusbproject.presentation.boards.courier

import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.presentation.base.BaseViewModel
import com.example.sampleusbproject.presentation.boards.adapter.CellsModel
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenedCourierBoardViewModel @Inject constructor(
    private val postomatUseCase: PostomatSocketUseCase
) : BaseViewModel() {

    private val _postomatCells = MutableStateFlow<List<CellsModel>>(listOf())
    val postomat = _postomatCells.asStateFlow()

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

}
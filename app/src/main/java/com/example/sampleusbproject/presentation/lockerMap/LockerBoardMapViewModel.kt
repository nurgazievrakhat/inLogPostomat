package com.example.sampleusbproject.presentation.lockerMap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleusbproject.domain.remote.socket.model.Cell
import com.example.sampleusbproject.domain.remote.socket.model.CellData
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import com.example.sampleusbproject.usecases.PostomatSocketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LockerBoardMapViewModel @Inject constructor(
    private val postomatSocketUseCase: PostomatSocketUseCase
) : ViewModel() {

    private val _postomatInfo = MutableLiveData<PostomatInfo>()
    val postomatInfo: LiveData<PostomatInfo> = _postomatInfo

    private val _selectedCell = MutableLiveData<Cell?>()
    val selectedCell: LiveData<Cell?> = _selectedCell

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _cellEvents = MutableLiveData<CellData>()
    val cellEvents: LiveData<CellData> = _cellEvents

    init {
        subscribeCellEvent()
    }

    fun selectCell(cell: Cell) {
        _selectedCell.value = cell
    }

    fun clearSelectedCell() {
        _selectedCell.value = null
    }

    fun subscribeCellEvent() {
        postomatSocketUseCase.observePostomatCellEvents { cellData ->
            _cellEvents.value = cellData
        }
    }
    fun openCell(cellId: String, boardId: String) {
        viewModelScope.launch {
            try {
                postomatSocketUseCase.sendCellCommand(cellId, boardId, true)
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при открытии ячейки")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        postomatSocketUseCase.disconnectFromPostomatServer()
    }
} 
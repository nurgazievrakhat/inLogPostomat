package com.example.sampleusbproject.lockBoards

import android.content.Context
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface

object LockBoardFactory {
    const val BOARD_TYPE_STANDARD = 1
    const val BOARD_TYPE_NEW = 2

    fun createBoard(context: Context, boardType: Int): LockerBoardInterface {
        return when (boardType) {
            BOARD_TYPE_STANDARD ->
                LockerControlBoard(context, 0x1A86, 0x7523)
            BOARD_TYPE_NEW ->
                NewLockerControlBoard(context)
            else -> throw IllegalArgumentException("Unknown board type: $boardType")
        }
    }
}
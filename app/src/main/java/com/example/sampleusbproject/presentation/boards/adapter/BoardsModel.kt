package com.example.sampleusbproject.presentation.boards.adapter

data class BoardsModel(
    val list: List<Board>,
    val heightItemCount: Int,
    val widthItemCount: Int
)

data class Board(
    val size: BoardSize,
    val number: Int,
    val usable: Boolean
)

enum class BoardSize(val ratioHeight: Int, val ratioWidth: Int) {
    S(1, 1),
    M(1, 2),
    L(2, 2),
    XL(3, 2);

    companion object {
        fun getIntType(b: BoardSize) = when(b){
            S -> 0
            M -> 1
            L -> 2
            XL -> 3
        }

        fun getType(type: Int) = when(type){
            0 -> S
            1 -> M
            2 -> L
            else -> XL
        }
    }

}
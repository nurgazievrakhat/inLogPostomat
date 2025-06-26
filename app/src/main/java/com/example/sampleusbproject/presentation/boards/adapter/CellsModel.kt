package com.example.sampleusbproject.presentation.boards.adapter

data class CellsModel(
    val list: List<CellSchema>,
    val columnItemCount: Int,
    val rawItemCount: Int
)

data class CellSchema(
    val size: BoardSize,
    val number: Long,
    val usable: Boolean
)

enum class BoardSize(val ratioHeight: Int, val ratioWidth: Int, val size: Int) {
    S(1, 1, 1),
    M(1, 2, 2),
    L(2, 2, 3),
    XL(3, 2, 4);

    companion object {
        fun getIntType(b: BoardSize) = when(b){
            S -> 0
            M -> 1
            L -> 2
            XL -> 3
        }

        fun fromString(s: String) = when(s.lowercase()){
            "xl" -> XL
            "l" -> L
            "m" -> M
            else -> S
        }

        fun getType(type: Int) = when(type){
            0 -> S
            1 -> M
            2 -> L
            else -> XL
        }
    }

}
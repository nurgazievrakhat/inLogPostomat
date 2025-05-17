package com.example.sampleusbproject.presentation.numberPad

enum class PackageType {
    COURIER,
    LEAVE,
    TAKE;

    companion object {
        fun getType(id: Int) = when(id){
            0 -> COURIER
            1 -> LEAVE
            else -> TAKE
        }
        fun getInt(id: PackageType) = when(id){
            COURIER -> 0
            LEAVE -> 1
            TAKE -> 2
        }
    }
}
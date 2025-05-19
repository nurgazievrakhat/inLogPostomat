package com.example.sampleusbproject.presentation.phoneNumber

enum class PhoneType {
    MINE,
    OTHER;

    companion object {
        fun getType(t: Int) = when(t){
            0 -> MINE
            else -> OTHER
        }

        fun getInt(type: PhoneType) = when(type){
            PhoneType.MINE -> 0
            PhoneType.OTHER -> 1
        }
    }
}
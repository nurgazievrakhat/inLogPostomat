package com.example.sampleusbproject.presentation.numberPad

import java.io.Serializable

data class PostomatTakeCell(
    val orderId: String,
    val id: String,
    val number: Long,
    val boardId: String
) : Serializable
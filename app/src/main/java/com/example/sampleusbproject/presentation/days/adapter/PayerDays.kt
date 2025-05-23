package com.example.sampleusbproject.presentation.days.adapter

enum class PayerDays(val list: List<Int>) {
    SENDER(listOf(1, 2, 3, 4, 5)),
    RECEIVER(listOf(1, 2))
}

fun PayerDays.getSelectList(selectedPos: Int) = this.list.map {
    SelectDay(
        isSelected = it == selectedPos,
        day = it
    )
}
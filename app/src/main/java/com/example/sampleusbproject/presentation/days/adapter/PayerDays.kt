package com.example.sampleusbproject.presentation.days.adapter

enum class PayerDays(val list: List<Int>) {
    SENDER(listOf(1, 2, 3, 4, 5)),
    RECEIVER(listOf(1, 2))
}

fun PayerDays.getSelectList(previousSelectedDay: Int? = null) = this.list.mapNotNull {
    if (it >= (previousSelectedDay ?: -1))
        SelectDay(
            isSelected = false,
            day = it,
            isAvailableToSelect = true
        )
    else
        null
}
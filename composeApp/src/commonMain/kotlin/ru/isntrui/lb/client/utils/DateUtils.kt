package ru.isntrui.lb.client.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(day: Int, month: Int): String {
    val monthNames = listOf(
        "января", "февраля", "марта", "апреля",
        "мая", "июня", "июля", "августа",
        "сентября", "октября", "ноября", "декабря"
    )

    return "$day ${monthNames[month - 1]}"
}

fun isDatePassed(date: LocalDate): Boolean {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return date < today
}
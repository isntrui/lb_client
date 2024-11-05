package ru.isntrui.lb.client.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Wave (
	val id : Int = 0,
	val title : String = "",
	val startsOn : LocalDate = LocalDate(2021, 1, 1),
	val endsOn : LocalDate = LocalDate(2021, 1, 14),
	val status : String = "",
	val songs : List<String> = emptyList(),
	val createdAt : LocalDate = LocalDate(2020, 12, 31),
)
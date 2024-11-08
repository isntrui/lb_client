package ru.isntrui.lb.client.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.enums.WaveStatus

@Serializable
data class Wave (
	val id : Int = 0,
	val title : String = "",
	val startsOn : LocalDate,
	val endsOn : LocalDate,
	val status : WaveStatus,
	val songs : Set<String> = emptySet(),
	val createdAt : LocalDate = LocalDate(2020, 12, 31),
)
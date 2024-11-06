package ru.isntrui.lb.client.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.task.UserTask

@Serializable
data class Song (
	val id : Long? = null,
	val title : String,
	val artist : String,
	val description : String,
	val url : String,
	val madeBy : User,
	val createdOn : LocalDateTime,
	val wave : Wave,
	val approvedBy : UserTask?,
	val approvedOn : LocalDateTime?,
	val approved : Boolean = false
)
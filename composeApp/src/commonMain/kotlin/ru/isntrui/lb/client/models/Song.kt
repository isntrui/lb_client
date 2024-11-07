package ru.isntrui.lb.client.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
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
	val createdOn : LocalDateTime = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()),
	val wave : Wave,
	val approvedBy : UserTask? = null,
	val approvedOn : LocalDateTime? = null,
	val approved : Boolean = false
)
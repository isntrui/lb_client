package ru.isntrui.lb.client.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.task.UserTask

@Serializable
data class TextI (
	val id : Long = 0,
	val wave : Wave? = null,
	val body : String = "",
	val title : String = "",
	val madeBy : User,
	val madeOn : LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
	val approvedBy : UserTask? = null,
	val approvedOn : LocalDateTime? = null,
	val approved : Boolean = false
)
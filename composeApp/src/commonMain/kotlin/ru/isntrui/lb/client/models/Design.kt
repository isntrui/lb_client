package ru.isntrui.lb.client.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.task.UserTask

@Serializable
data class Design (
	val id : Long,
	val createdBy : User,
	val createdOn : LocalDateTime,
	val wave : Wave,
	val approvedBy : UserTask?,
	val approvedOn : LocalDateTime?,
	val url : String,
	val title : String,
	val approved : Boolean
)
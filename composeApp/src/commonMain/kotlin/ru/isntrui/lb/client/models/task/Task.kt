package ru.isntrui.lb.client.models.task

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.TaskStatus

@Serializable
data class Task (
	val id : Int,
	val title : String,
	val description : String,
	val deadline : LocalDate,
	val createdOn : LocalDateTime,
	val createdBy : UserTask,
	val taskStatus : TaskStatus = TaskStatus.TODO,
	val wave : Wave,
	val takenBy : UserTask?,
	val takenOn : String?,
	val madeOn : String?,
	val completed : Boolean,
	val shown : Boolean
) {
}
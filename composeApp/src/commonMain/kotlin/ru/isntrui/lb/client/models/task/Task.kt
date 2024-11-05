package ru.isntrui.lb.client.models.task

import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.Wave

@Serializable
data class Task (
	val id : Int,
	val title : String,
	val description : String,
	val deadline : LocalDate,
	val createdOn : String,
	val createdBy : UserTask,
	val taskStatus : String = "TODO",
	val wave : Wave,
	val takenBy : UserTask?,
	val takenOn : String?,
	val madeOn : String?,
	val completed : Boolean,
	val shown : Boolean
) {
}
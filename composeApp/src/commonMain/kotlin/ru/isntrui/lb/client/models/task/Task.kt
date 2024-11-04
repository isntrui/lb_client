package ru.isntrui.lb.client.models.task

import kotlinx.serialization.Serializable

@Serializable
data class Task (
	val id : Int,
	val title : String,
	val description : String,
	val deadline : String,
	val createdOn : String,
	val createdBy : UserTask,
	val taskStatus : String,
	val wave : String?,
	val takenBy : UserTask?,
	val takenOn : String?,
	val madeOn : String?,
	val completed : Boolean,
	val shown : Boolean
)
package ru.isntrui.lb.client.models.task

import ru.isntrui.lb.client.models.enums.Role

import kotlinx.serialization.Serializable


@Serializable
data class UserTask (
	val id : Int,
	val role : Role,
	val firstName : String,
	val lastName : String,
	val graduateYear : Int,
	val building : String,
	val registeredOn : String,
	val username : String,
	val avatarUrl : String?,
)
package ru.isntrui.lb.client.models.task

import ru.isntrui.lb.client.models.enums.Role

import kotlinx.serialization.Serializable


@Serializable
data class UserTask (
	val id : Long,
	val role : Role? = null,
	val firstName : String? = null,
	val lastName : String? = null,
	val graduateYear : Int? = null,
	val building : String? = null,
	val registeredOn : String? = null,
	val username : String? = null,
	val avatarUrl : String? = null,
)
package ru.isntrui.lb.client.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.enums.Role

@Serializable
data class User(
    val id: Long = 0,
    val email: String = "",
    val password: String = "",
    val role: Role = Role.GRADUATED,
    val firstName: String = "",
    val lastName: String = "",
    val graduateYear: Int = 2024,
    val building: String = "",
    val registeredOn: LocalDateTime? = null,
    val tgUsername: String? = null,
    val username: String = "",
    val avatarUrl: String? = null,
)
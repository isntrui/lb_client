package ru.isntrui.lb.client.requests

import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.enums.Role

@Serializable
data class SignUpRequest(
    val role: Role? = null,
    val firstName: String = "",
    val lastName: String = "",
    val building: String = "",
    val year: Int,
    val inviteCode: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = ""
)
package ru.isntrui.lb.client.requests

import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.enums.Role

@Serializable
data class SignUpRequest(
    val role: Role? = null, // done
    val firstName: String = "", // done
    val lastName: String = "", // done
    val building: String = "", // done
    val year: Int, // done
    val inviteCode: String = "", // done
    val username: String = "", //
    val email: String = "", // TODO
    val password: String = "" // TODO
)
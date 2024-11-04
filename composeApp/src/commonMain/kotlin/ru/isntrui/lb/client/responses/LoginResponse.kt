package ru.isntrui.lb.client.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String)
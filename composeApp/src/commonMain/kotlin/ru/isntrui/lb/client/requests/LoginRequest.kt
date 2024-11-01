package ru.isntrui.lb.client.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

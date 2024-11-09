package ru.isntrui.lb.client.models

import kotlinx.serialization.Serializable

@Serializable
data class Invite(
    val code: String,
    val email: String
)

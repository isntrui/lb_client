package ru.isntrui.lb.client.requests

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.task.UserTask

@Serializable
data class TaskRequest(
    var createdBy : User,
    var wave : Wave,
    var title : String = "",
    var description : String = "",
    var deadline : LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    var takenBy : UserTask?,
    var takenOn : LocalDate? = null,
)

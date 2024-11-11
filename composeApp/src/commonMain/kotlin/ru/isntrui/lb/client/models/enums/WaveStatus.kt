package ru.isntrui.lb.client.models.enums

import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.finished_statuses
import lbtool.composeapp.generated.resources.planned_statuses
import lbtool.composeapp.generated.resources.progress_statuses
import org.jetbrains.compose.resources.StringResource

enum class WaveStatus(val res: StringResource) {
    PLANNED(Res.string.planned_statuses),
    STARTED(Res.string.progress_statuses),
    FINISHED(Res.string.finished_statuses)
}
package ru.isntrui.lb.client.models.enums

import lbtool.composeapp.generated.resources.ADMIN
import lbtool.composeapp.generated.resources.COORDINATOR
import lbtool.composeapp.generated.resources.DESIGNER
import lbtool.composeapp.generated.resources.GRADUATED
import lbtool.composeapp.generated.resources.HEAD
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.SOUNDDESIGNER
import lbtool.composeapp.generated.resources.TECHNICAL
import lbtool.composeapp.generated.resources.WRITER
import org.jetbrains.compose.resources.StringResource

enum class Role(val res: StringResource) {
    GRADUATED(Res.string.GRADUATED),
    TECHNICAL(Res.string.TECHNICAL),
    DESIGNER(Res.string.DESIGNER),
    WRITER(Res.string.WRITER),
    SOUNDDESIGNER(Res.string.SOUNDDESIGNER),
    COORDINATOR(Res.string.COORDINATOR),
    HEAD(Res.string.HEAD),
    ADMIN(Res.string.ADMIN)
}
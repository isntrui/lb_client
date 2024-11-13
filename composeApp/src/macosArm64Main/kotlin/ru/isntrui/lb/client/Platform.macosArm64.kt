package ru.isntrui.lb.client

class JVMPlatform: Platform {
    override val name: String = "MacOS (Arm64)"
}

actual fun getPlatform(): Platform = JVMPlatform()
package ru.isntrui.lb.client

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
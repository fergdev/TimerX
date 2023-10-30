package com.timerx

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.timerx

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, Fanchao ${platform.name}!"
    }
}
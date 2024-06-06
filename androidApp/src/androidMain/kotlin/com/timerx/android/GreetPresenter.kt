package com.timerx.android

import com.timerx.Greeting

class GreetPresenter(val greeting: Greeting) {
    fun print() = greeting.greeting()
}
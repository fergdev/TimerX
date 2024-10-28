package com.timerx.util

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration.Companion.seconds

class ProjectConfig : AbstractProjectConfig() {
    override var coroutineTestScope = true
    override var timeout = 5.seconds
}

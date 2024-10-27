package com.timerx.util

import com.timerx.BuildFlags
import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

val disableForAndroid: (TestCase) -> Boolean = { BuildFlags.platform != Platform.Android }

class NotAndroidCondition : EnabledCondition {
    override fun enabled(kclass: KClass<out Spec>): Boolean =
        BuildFlags.platform != Platform.Android
}

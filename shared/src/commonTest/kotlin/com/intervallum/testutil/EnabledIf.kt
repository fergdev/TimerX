package com.intervallum.testutil

import com.intervallum.BuildFlags
import com.intervallum.util.Platform
import com.intervallum.util.platform
import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

val disableForAndroid: (TestCase) -> Boolean = { BuildFlags.platform != Platform.Android }

class NotAndroidCondition : EnabledCondition {
    override fun enabled(kclass: KClass<out Spec>): Boolean =
        BuildFlags.platform != Platform.Android
}

class DisabledCondition : EnabledCondition {
    override fun enabled(kclass: KClass<out Spec>): Boolean = false
}

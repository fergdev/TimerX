package com.intervallum.vibration

import com.intervallum.vibration.Vibration.Heavy
import com.intervallum.vibration.Vibration.HeavyX2
import com.intervallum.vibration.Vibration.HeavyX3
import com.intervallum.vibration.Vibration.Light
import com.intervallum.vibration.Vibration.LightX2
import com.intervallum.vibration.Vibration.LightX3
import com.intervallum.vibration.Vibration.Medium
import com.intervallum.vibration.Vibration.MediumX2
import com.intervallum.vibration.Vibration.MediumX3
import com.intervallum.vibration.Vibration.None
import com.intervallum.vibration.Vibration.Rigid
import com.intervallum.vibration.Vibration.RigidX2
import com.intervallum.vibration.Vibration.RigidX3
import com.intervallum.vibration.Vibration.Soft
import com.intervallum.vibration.Vibration.SoftX2
import com.intervallum.vibration.Vibration.SoftX3
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VibrationTest : FreeSpec({

    "to millis" - {
        Soft.toMillis() shouldBe 100L
        SoftX2.toMillis() shouldBe 100L
        SoftX3.toMillis() shouldBe 100L
        Light.toMillis() shouldBe 200L
        LightX2.toMillis() shouldBe 200L
        LightX3.toMillis() shouldBe 200L
        Rigid.toMillis() shouldBe 300L
        RigidX2.toMillis() shouldBe 300L
        RigidX3.toMillis() shouldBe 300L
        Medium.toMillis() shouldBe 400L
        MediumX2.toMillis() shouldBe 400L
        MediumX3.toMillis() shouldBe 400L
        Heavy.toMillis() shouldBe 500L
        HeavyX2.toMillis() shouldBe 500L
        HeavyX3.toMillis() shouldBe 500L
        None.toMillis() shouldBe 0L
    }
    "repeat" - {
        Soft.repeat shouldBe 1
        SoftX2.repeat shouldBe 2
        SoftX3.repeat shouldBe 3
        Light.repeat shouldBe 1
        LightX2.repeat shouldBe 2
        LightX3.repeat shouldBe 3
        Rigid.repeat shouldBe 1
        RigidX2.repeat shouldBe 2
        RigidX3.repeat shouldBe 3
        Medium.repeat shouldBe 1
        MediumX2.repeat shouldBe 2
        MediumX3.repeat shouldBe 3
        Heavy.repeat shouldBe 1
        HeavyX2.repeat shouldBe 2
        HeavyX3.repeat shouldBe 3
        None.repeat shouldBe 0
    }
    "display name" - {
        Soft.displayName shouldBe "Soft"
        SoftX2.displayName shouldBe "Soft X2"
        SoftX3.displayName shouldBe "Soft X3"
        Light.displayName shouldBe "Light"
        LightX2.displayName shouldBe "Light X2"
        LightX3.displayName shouldBe "Light X3"
        Rigid.displayName shouldBe "Rigid"
        RigidX2.displayName shouldBe "Rigid X2"
        RigidX3.displayName shouldBe "Rigid X3"
        Medium.displayName shouldBe "Medium"
        MediumX2.displayName shouldBe "Medium X2"
        MediumX3.displayName shouldBe "Medium X3"
        Heavy.displayName shouldBe "Heavy"
        HeavyX2.displayName shouldBe "Heavy X2"
        HeavyX3.displayName shouldBe "Heavy X3"
        None.displayName shouldBe "None"
    }
})

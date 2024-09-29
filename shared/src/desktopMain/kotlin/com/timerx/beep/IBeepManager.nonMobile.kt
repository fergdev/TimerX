package com.timerx.beep

object BeepManager : IBeepManager {
    override suspend fun beep(beep: Beep) {
        println("Beeeep $beep")
    }
}

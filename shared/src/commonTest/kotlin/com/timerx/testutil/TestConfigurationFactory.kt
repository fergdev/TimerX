package com.timerx.testutil

import com.timerx.ui.di.ConfigurationFactory
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.serialization.KSerializer
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.api.StateStrategy
import pro.respawn.flowmvi.dsl.StoreBuilder
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.savedstate.api.Saver
import pro.respawn.flowmvi.savedstate.dsl.NoOpSaver

object TestConfigurationFactory : ConfigurationFactory {

    override fun <S : MVIState, I : MVIIntent, A : MVIAction> StoreBuilder<S, I, A>.invoke(
        name: String,
        saver: Saver<S>?,
    ) {
        configure {
            this.name = name
            parallelIntents = false
            stateStrategy = StateStrategy.Immediate
            onOverflow = BufferOverflow.SUSPEND
        }
        enableLogging()
    }

    override fun <S : MVIState> saver(serializer: KSerializer<S>, fileName: String) = NoOpSaver<S>()
}

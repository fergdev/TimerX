package com.timerx.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.timerx.database.ITimerRepository
import com.timerx.database.TimerRepository
import com.timerx.database.createRoomDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mobileModule = module {
    single {
        createRoomDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    singleOf(::TimerRepository) { bind<ITimerRepository>() }
}

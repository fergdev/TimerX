package com.timerx.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.mp.KoinPlatform

class Factory : RoomDataBaseFactory  {
    private val context : Context = KoinPlatform.getKoin().get()
    override fun createRoomDataBase(): AppDatabase {
        val dbFile = context.getDatabasePath(dbFileName)
        return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

actual fun createRoomDatabaseFactory(): RoomDataBaseFactory {
    return Factory()
}
@file:Suppress("Filename")

package com.timerx.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform

actual fun createRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context: Context = KoinPlatform.getKoin().get()
    val dbFile = context.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
}

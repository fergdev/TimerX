package com.timerx.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun createRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), DB_FILE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
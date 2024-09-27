@file:Suppress("Filename")

package com.timerx.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun fileDirectory(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path!!
}

actual fun createRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = "${fileDirectory()}/$DB_FILE_NAME"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile,
    )
}

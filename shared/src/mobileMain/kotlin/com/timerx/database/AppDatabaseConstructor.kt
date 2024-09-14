package com.timerx.database

import androidx.room.RoomDatabaseConstructor

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    // Added to fix issue where this was not overridden, consider removing in future
    override fun initialize(): AppDatabase
}

package com.timerx.database

import androidx.room.RoomDatabase

expect fun createRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

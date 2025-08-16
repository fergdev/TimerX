package com.intervallum.database

import androidx.room.RoomDatabase

expect fun createRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

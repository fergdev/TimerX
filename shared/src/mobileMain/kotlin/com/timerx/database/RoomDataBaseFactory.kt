package com.timerx.database

interface RoomDataBaseFactory {
    fun createRoomDataBase(): AppDatabase
}

expect fun createRoomDatabaseFactory(): RoomDataBaseFactory

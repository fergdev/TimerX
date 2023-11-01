package com.timerx.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import sqldelight.AppDatabase

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactoryImpl()

class DatabaseDriverFactoryImpl : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "test.db")
    }
}
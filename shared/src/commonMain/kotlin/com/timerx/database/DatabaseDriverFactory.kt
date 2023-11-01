package com.timerx.database

import com.squareup.sqldelight.db.SqlDriver

expect fun getDatabaseDriverFactory(): DatabaseDriverFactory

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
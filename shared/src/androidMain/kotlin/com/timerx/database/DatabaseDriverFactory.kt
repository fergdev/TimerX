package com.timerx.database

import android.app.Application
import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import sqldelight.AppDatabase

lateinit var application: Application

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactoryImpl(application)

class DatabaseDriverFactoryImpl(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "test.db")
    }
}
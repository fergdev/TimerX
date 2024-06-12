package com.timerx.settings


interface ISettingsManager {
    fun getString(key: String, defaultValue: String? = null): String?
    fun putString(key: String, value: String)

    fun getInt(key: String, defaultValue: Int = 0): Int
    fun putInt(key: String, value: Int)

    fun getFloat(key: String, defaultValue: Float = 0F): Float
    fun putFloat(key: String, value: Float)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
}

expect fun getSettingsManager(): ISettingsManager
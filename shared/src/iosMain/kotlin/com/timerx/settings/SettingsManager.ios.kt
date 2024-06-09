package com.timerx.settings

import platform.Foundation.NSUserDefaults

actual class SettingsManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String, defaultValue: String?): String? {
        return userDefaults.stringForKey(key) ?: defaultValue
    }

    actual fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    actual fun getInt(key: String, defaultValue: Int): Int {
        return userDefaults.integerForKey(key).toInt()
    }

    actual fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), forKey = key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return userDefaults.boolForKey(key)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, forKey = key)
    }

    actual fun getFloat(key: String, defaultValue: Float): Float {
        return userDefaults.floatForKey(key)
    }

    actual fun putFloat(key: String, value: Float) {
        userDefaults.setFloat(value, forKey = key)
    }
}
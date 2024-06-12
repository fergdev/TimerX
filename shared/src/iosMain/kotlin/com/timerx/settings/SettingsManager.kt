package com.timerx.settings

import platform.Foundation.NSUserDefaults

class IosSettingsManager : ISettingsManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String, defaultValue: String?): String? {
        return userDefaults.stringForKey(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return userDefaults.integerForKey(key).toInt()
    }

    override fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), forKey = key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return userDefaults.boolForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, forKey = key)
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return userDefaults.floatForKey(key)
    }

    override fun putFloat(key: String, value: Float) {
        userDefaults.setFloat(value, forKey = key)
    }
}

actual fun getSettingsManager() : ISettingsManager = IosSettingsManager()

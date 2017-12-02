package com.greg.settings

import java.util.prefs.Preferences

class Settings {
    companion object {
        private val prefs = Preferences.userNodeForPackage(Settings::class.java!!)

        fun put(setting: SettingsKey, value: String) {
            prefs.put(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Boolean) {
            prefs.putBoolean(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Int) {
            prefs.putInt(setting.key, value)
        }

        fun contains(setting: SettingsKey): Boolean {
            return prefs.get(setting.key, setting.default as String) != setting.default
        }

        fun get(setting: SettingsKey): String {
            return prefs.get(setting.key, setting.default as String)
        }

        fun getBoolean(setting: SettingsKey): Boolean {
            return prefs.getBoolean(setting.key, setting.default as Boolean)
        }

        fun getInt(setting: SettingsKey): Int {
            return prefs.getInt(setting.key, setting.default as Int)
        }
    }
}
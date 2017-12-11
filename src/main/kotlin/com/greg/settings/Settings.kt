package com.greg.settings

import javafx.scene.paint.Color
import java.util.prefs.Preferences

class Settings {
    companion object {
        private val preferences = Preferences.userNodeForPackage(Settings::class.java)

        fun contains(setting: SettingsKey): Boolean {
            return preferences.get(setting.key, setting.default as String) != setting.default
        }

        fun put(setting: SettingsKey, value: String) {
            preferences.put(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Boolean) {
            preferences.putBoolean(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Int) {
            preferences.putInt(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Double) {
            preferences.putDouble(setting.key, value)
        }

        fun get(setting: SettingsKey): String {
            return preferences.get(setting.key, setting.default as String)
        }

        fun getBoolean(setting: SettingsKey): Boolean {
            return preferences.getBoolean(setting.key, setting.default as Boolean)
        }

        fun getInt(setting: SettingsKey): Int {
            return preferences.getInt(setting.key, setting.default as Int)
        }

        fun getDouble(setting: SettingsKey): Double {
            return preferences.getDouble(setting.key, setting.default as Double)
        }

        fun getColour(setting: SettingsKey): Color? {
            val colour = get(setting)
            return Color.valueOf(colour)
        }
    }
}
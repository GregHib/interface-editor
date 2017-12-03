package com.greg.settings

import javafx.scene.paint.Color
import java.util.prefs.Preferences

class Settings {
    companion object {
        private val prefs = Preferences.userNodeForPackage(Settings::class.java)

        fun contains(setting: SettingsKey): Boolean {
            return prefs.get(setting.key, setting.default as String) != setting.default
        }

        fun put(setting: SettingsKey, value: String) {
            prefs.put(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Boolean) {
            prefs.putBoolean(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Int) {
            prefs.putInt(setting.key, value)
        }

        fun put(setting: SettingsKey, value: Double) {
            prefs.putDouble(setting.key, value)
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

        fun getDouble(setting: SettingsKey): Double {
            return prefs.getDouble(setting.key, setting.default as Double)
        }

        fun getColour(setting: SettingsKey): Color? {
            val colour = get(setting)
            return Color.valueOf(colour)
        }
    }
}
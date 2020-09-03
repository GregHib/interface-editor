package com.greg.model.settings

import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import java.util.prefs.Preferences

enum class Settings(var default: Any) {
    CANCEL_ON_UNFOCUSED(false),
    ACCEPT_KEY_CODE(KeyCode.ENTER.ordinal),
    CANCEL_KEY_CODE(KeyCode.ESCAPE.ordinal),
    SELECTION_STROKE_COLOUR(Color.RED.toString()),
    DEFAULT_STROKE_COLOUR(Color.TRANSPARENT.toString()),
    SELECTION_STROKE_ANIMATE(true),
    SELECTION_STROKE_ANIMATION_DURATION(1.0),
    DEFAULT_TEXT_MESSAGE("Text"),
    DEFAULT_TEXT_SECONDARY_MESSAGE(""),
    DEFAULT_TEXT_DEFAULT_COLOUR(Color.BLACK.toString()),
    DEFAULT_TEXT_SECONDARY_COLOUR(Color.BLACK.toString()),
    DEFAULT_TEXT_DEFAULT_HOVER_COLOUR(Color.BLACK.toString()),
    DEFAULT_TEXT_SECONDARY_HOVER_COLOUR(Color.BLACK.toString()),
    DEFAULT_TEXT_CENTRED(false),
    DEFAULT_TEXT_SHADOW(false),
    DEFAULT_POSITION_X(0),
    DEFAULT_POSITION_Y(0),
    DEFAULT_RECTANGLE_WIDTH(50),
    DEFAULT_RECTANGLE_HEIGHT(50),
    DEFAULT_RECTANGLE_DEFAULT_COLOUR(Color.CADETBLUE.toString()),
    DEFAULT_RECTANGLE_SECONDARY_COLOUR(Color.BLACK.toString()),
    DEFAULT_RECTANGLE_DEFAULT_HOVER_COLOUR(Color.BLACK.toString()),
    DEFAULT_RECTANGLE_SECONDARY_HOVER_COLOUR(Color.BLACK.toString()),
    DEFAULT_RECTANGLE_FILLED(true),
    DEFAULT_CONTAINER_SCROLL_LIMIT(100),
    WIDGET_CANVAS_WIDTH(765),
    WIDGET_CANVAS_HEIGHT(503),
    DEFAULT_WIDGET_MINIMUM_WIDTH(12),
    DEFAULT_WIDGET_MINIMUM_HEIGHT(12),
    DEFAULT_WIDGET_RESIZE_TAB_WIDTH(4.0),
    DEFAULT_WIDGET_RESIZE_TAB_HEIGHT(4.0),
    DISABLE_LOCK_ON_UNHIDDEN(false),
    DEFAULT_SPRITE_ID(0),
    DEFAULT_SPRITE_ARCHIVE(0),
    DEFAULT_WIDGET_LIST_IMAGE_WIDTH(400.0),
    DEFAULT_WIDGET_LIST_IMAGE_HEIGHT(200.0),
    SPRITE_RESAMPLING(false),
    DEFAULT_HAS_ACTIONS(false),
    NIGHT_MODE(true),
    HIERARCHY_OPEN_NEW_CONTAINERS(true),
    CACHE_DIRECTORY("")
    ;

    val key: String = this.name
    companion object {
        private val preferences = Preferences.userNodeForPackage(Settings::class.java)

        fun clear() {
            preferences.clear()
        }

        fun contains(setting: Settings): Boolean {
            return preferences.get(setting.key, setting.default as String) != setting.default
        }

        fun put(setting: Settings, value: String) {
            preferences.put(setting.key, value)
        }

        fun put(setting: Settings, value: Boolean) {
            preferences.putBoolean(setting.key, value)
        }

        fun put(setting: Settings, value: Int) {
            preferences.putInt(setting.key, value)
        }

        fun put(setting: Settings, value: Double) {
            preferences.putDouble(setting.key, value)
        }

        fun get(setting: Settings): String {
            return preferences.get(setting.key, setting.default as String)
        }

        fun getBoolean(setting: Settings): Boolean {
            return preferences.getBoolean(setting.key, setting.default as Boolean)
        }

        fun getInt(setting: Settings): Int {
            return preferences.getInt(setting.key, setting.default as Int)
        }

        fun getDouble(setting: Settings): Double {
            return preferences.getDouble(setting.key, setting.default as Double)
        }

        fun getColour(setting: Settings): Color {
            val colour = get(setting)
            return Color.valueOf(colour)
        }
    }
}
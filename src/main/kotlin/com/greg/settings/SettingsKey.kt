package com.greg.settings

enum class SettingsKey(val key: String, var default: Any) {
    CANCEL_ON_DEFOCUS("cancel on defocus", false);
}
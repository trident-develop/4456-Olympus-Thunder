package com.flipkart.sho.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chess_settings", Context.MODE_PRIVATE)

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC, true)
        set(value) = prefs.edit { putBoolean(KEY_MUSIC, value) }

    var isHelpEnabled: Boolean
        get() = prefs.getBoolean(KEY_HELP, true)
        set(value) = prefs.edit { putBoolean(KEY_HELP, value) }

    companion object {
        private const val KEY_MUSIC = "music_enabled"
        private const val KEY_HELP = "help_enabled"
    }
}

package dev.sakura.core.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS_NAME = "ThemePrefs"
    private const val KEY_SELECTED_THEME = "selected_theme"

    const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun applyTheme(themePreference: Int) {
        AppCompatDelegate.setDefaultNightMode(themePreference)
    }

    fun saveThemePreference(context: Context, themePreference: Int) {
        getPreferences(context).edit().putInt(KEY_SELECTED_THEME, themePreference).apply()
        applyTheme(themePreference)
    }

    fun getSavedThemePreference(context: Context): Int {
        return getPreferences(context).getInt(KEY_SELECTED_THEME, THEME_SYSTEM)
    }
}

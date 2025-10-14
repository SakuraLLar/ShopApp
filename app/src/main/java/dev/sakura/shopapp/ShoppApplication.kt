package dev.sakura.shopapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.sakura.core.util.ThemeManager

@HiltAndroidApp
class ShoppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val savedTheme = ThemeManager.getSavedThemePreference(this)
        ThemeManager.applyTheme(savedTheme)
    }
}

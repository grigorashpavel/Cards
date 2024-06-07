package com.pasha.profile.api


import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


class PreferencesManager(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val themeCode get() = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES)
    internal val isDarkTheme get() = themeCode == AppCompatDelegate.MODE_NIGHT_YES

    internal fun setThemeType(isDark: Boolean) {
        if (isDark) {
            preferences.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES).apply()
        } else {
            preferences.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO).apply()
        }
    }

    companion object {
        private const val THEME_KEY = "CardsTheme"
    }
}
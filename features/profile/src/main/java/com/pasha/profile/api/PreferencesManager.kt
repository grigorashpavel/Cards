package com.pasha.profile.api


import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


class PreferencesManager(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private enum class Languages(val locale: String) {
        English("en"),
        Russian("ru"),
    }

    val languageCode get() = preferences.getInt(LANGUAGE_KEY, Languages.English.ordinal)
    internal val languages get() = Languages.entries.map { it.name }.toTypedArray()

    val themeCode get() = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES)
    internal val isDarkTheme get() = themeCode == AppCompatDelegate.MODE_NIGHT_YES

    internal fun setThemeType(isDark: Boolean) {
        if (isDark) {
            preferences.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES).apply()
        } else {
            preferences.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO).apply()
        }
    }

    internal fun setLanguage(langCode: Int) {
        preferences.edit().putInt(LANGUAGE_KEY, langCode).apply()
    }

    companion object {
        private const val THEME_KEY = "CardsTheme"
        private const val LANGUAGE_KEY = "CardsLang"

        fun getLanguageType(code: Int): String {
            val language = Languages.entries.find { it.ordinal == code } ?: Languages.English
            return language.locale
        }
    }
}
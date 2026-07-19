package com.tech.cropify.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    // Maps display name → BCP-47 locale tag
    val supportedLanguages = linkedMapOf(
        "English"           to "en",
        "हिंदी (Hindi)"     to "hi",
        "मराठी (Marathi)"   to "mr",
        "ਪੰਜਾਬੀ (Punjabi)" to "pa"
    )

    fun getSavedLanguageCode(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun saveLanguageCode(context: Context, code: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, code)
            .apply()
    }

    fun applyLanguage(context: Context, code: String): Context {
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    /** Call this in Activity.attachBaseContext to apply saved language on every launch. */
    fun wrap(base: Context): Context {
        val code = getSavedLanguageCode(base)
        return applyLanguage(base, code)
    }
}
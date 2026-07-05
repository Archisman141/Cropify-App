package com.tech.cropify.util

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


val Context.dataStore by preferencesDataStore(
    name = "app_preference"
)

@Singleton
class ThemePreference @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore = context.dataStore

    companion object{
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeFlow: Flow<ThemeMode> =
        dataStore.data.map { prefs: Preferences ->
            ThemeMode.valueOf(
                prefs[THEME_KEY] ?: ThemeMode.SYSTEM.name
            )
        }

    suspend fun saveTheme(mode: ThemeMode) {
        dataStore.edit { prefs: MutablePreferences ->
            prefs[THEME_KEY] = mode.name
        }
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
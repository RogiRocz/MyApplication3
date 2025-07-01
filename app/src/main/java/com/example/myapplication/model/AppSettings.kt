package com.example.myapplication.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object UserPreferencesKeys {
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
    val PRIMARY_COLOR = stringPreferencesKey("primary_color")
    val SECONDARY_COLOR = stringPreferencesKey("secondary_color")
}

class UserPreferencesRepository(private val context: Context) {

    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.DARK_MODE_ENABLED] ?: false
        }

    suspend fun setDarkModeEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.DARK_MODE_ENABLED] = isEnabled
        }
    }

    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = isEnabled
        }
    }

    val areAnimationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.ANIMATIONS_ENABLED] ?: true
        }

    suspend fun setAnimationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.ANIMATIONS_ENABLED] = isEnabled
        }
    }
}
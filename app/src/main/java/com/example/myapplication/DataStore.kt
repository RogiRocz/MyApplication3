package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.dataStore
import com.example.myapplication.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_prefs")

object SettingsDataStore {
    private val DARK_KEY          = booleanPreferencesKey("dark_mode")
    private val NOTIF_KEY         = booleanPreferencesKey("notifications_enabled")
    private val ANIM_KEY          = booleanPreferencesKey("animations_enabled")

    val Context.appSettingsFlow: Flow<AppSettings>
        get() = dataStore.data.map { prefs ->
            AppSettings(
                darkModeEnabled      = prefs[DARK_KEY]  ?: false,
                notificationsEnabled = prefs[NOTIF_KEY] ?: true,
                animationsEnabled    = prefs[ANIM_KEY]  ?: true
            )
        }

    private suspend fun Context.update(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, v: Boolean) {
        dataStore.edit { it[key] = v }
    }

    suspend fun Context.setDarkMode(v: Boolean)      = update(DARK_KEY, v)
    suspend fun Context.setNotifications(v: Boolean) = update(NOTIF_KEY, v)
    suspend fun Context.setAnimations(v: Boolean)    = update(ANIM_KEY, v)
}
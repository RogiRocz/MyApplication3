package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.AppSettings // Removido import não utilizado de dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extensão para o Context que fornece acesso ao DataStore de preferências do aplicativo.
 * O nome "app_prefs" é usado para identificar este DataStore específico.
 */
private val Context.dataStore by preferencesDataStore("app_prefs")

/**
 * Objeto singleton para gerenciar as configurações do aplicativo armazenadas no DataStore.
 * Fornece métodos para ler e atualizar as preferências de modo escuro, notificações e animações.
 */
object SettingsDataStore {
    // Chaves para as preferências armazenadas no DataStore.
    private val DARK_KEY          = booleanPreferencesKey("dark_mode")
    private val NOTIF_KEY         = booleanPreferencesKey("notifications_enabled")
    private val ANIM_KEY          = booleanPreferencesKey("animations_enabled")

    /**
     * Fluxo (Flow) que emite as configurações atuais do aplicativo (`AppSettings`).
     * Ele observa as mudanças no DataStore e mapeia as preferências para um objeto `AppSettings`.
     *
     * @return Um Flow que emite o objeto `AppSettings` mais recente.
     */
    val Context.appSettingsFlow: Flow<AppSettings>
        get() = dataStore.data.map { prefs ->
            // Mapeia as preferências para o objeto AppSettings.
            // Se uma preferência não estiver definida, usa um valor padrão.
            AppSettings(
                darkModeEnabled      = prefs[DARK_KEY]  ?: false, // Padrão: modo escuro desabilitado
                notificationsEnabled = prefs[NOTIF_KEY] ?: true,  // Padrão: notificações habilitadas
                animationsEnabled    = prefs[ANIM_KEY]  ?: true   // Padrão: animações habilitadas
            )
        }

    /**
     * Função de extensão privada para atualizar um valor booleano no DataStore.
     *
     * @param key A chave da preferência a ser atualizada.
     * @param v O novo valor booleano para a preferência.
     */
    private suspend fun Context.update(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, v: Boolean) {
        dataStore.edit { it[key] = v }
    }

    /**
     * Atualiza a preferência de modo escuro.
     *
     * @param v `true` para habilitar o modo escuro, `false` para desabilitar.
     */
    suspend fun Context.setDarkMode(v: Boolean)      = update(DARK_KEY, v)

    /**
     * Atualiza a preferência de notificações.
     *
     * @param v `true` para habilitar as notificações, `false` para desabilitar.
     */
    suspend fun Context.setNotifications(v: Boolean) = update(NOTIF_KEY, v)

    /**
     * Atualiza a preferência de animações.
     *
     * @param v `true` para habilitar as animações, `false` para desabilitar.
     */
    suspend fun Context.setAnimations(v: Boolean)    = update(ANIM_KEY, v)
}
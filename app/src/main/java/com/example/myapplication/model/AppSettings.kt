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

/**
 * Define as configurações do aplicativo que podem ser persistidas.
 *
 * @property darkModeEnabled Indica se o modo escuro está ativado. O valor padrão é `false`.
 * @property notificationsEnabled Indica se as notificações estão ativadas. O valor padrão é `true`.
 * @property animationsEnabled Indica se as animações estão ativadas. O valor padrão é `true`.
 */
data class AppSettings(
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)

/**
 * Extensão para o Context que fornece acesso ao DataStore de preferências chamado "settings".
 * Este DataStore é usado para persistir as preferências do usuário.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Objeto que contém as chaves usadas para armazenar e recuperar preferências do DataStore.
 * Cada chave é tipada (por exemplo, `booleanPreferencesKey`, `stringPreferencesKey`) para garantir
 * a segurança de tipo ao acessar as preferências.
 */
object UserPreferencesKeys {
    /** Chave para a preferência de ativação do modo escuro. */
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    /** Chave para a preferência de ativação de notificações. */
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    /** Chave para a preferência de ativação de animações. */
    val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
    /** Chave para a preferência da cor primária (não utilizada atualmente neste exemplo). */
    val PRIMARY_COLOR = stringPreferencesKey("primary_color")
    /** Chave para a preferência da cor secundária (não utilizada atualmente neste exemplo). */
    val SECONDARY_COLOR = stringPreferencesKey("secondary_color")
}

/**
 * Repositório para gerenciar as preferências do usuário armazenadas no DataStore.
 * Fornece Flows para observar mudanças nas preferências e funções suspend para atualizá-las.
 *
 * @param context O contexto do aplicativo, usado para acessar o DataStore.
 */
class UserPreferencesRepository(private val context: Context) {

    /**
     * Um Flow que emite `true` se o modo escuro estiver ativado, `false` caso contrário.
     * Observa a chave [UserPreferencesKeys.DARK_MODE_ENABLED] no DataStore.
     * O valor padrão é `false` (modo escuro desativado).
     */
    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.DARK_MODE_ENABLED] ?: false
        }

    /**
     * Define o estado de ativação do modo escuro.
     *
     * @param isEnabled `true` para ativar o modo escuro, `false` para desativar.
     */
    suspend fun setDarkModeEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.DARK_MODE_ENABLED] = isEnabled
        }
    }

    /**
     * Um Flow que emite `true` se as notificações estiverem ativadas, `false` caso contrário.
     * Observa a chave [UserPreferencesKeys.NOTIFICATIONS_ENABLED] no DataStore.
     * O valor padrão é `true` (notificações ativadas).
     */
    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    /**
     * Define o estado de ativação das notificações.
     *
     * @param isEnabled `true` para ativar as notificações, `false` para desativar.
     */
    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = isEnabled
        }
    }

    /**
     * Um Flow que emite `true` se as animações estiverem ativadas, `false` caso contrário.
     * Observa a chave [UserPreferencesKeys.ANIMATIONS_ENABLED] no DataStore.
     * O valor padrão é `true` (animações ativadas).
     */
    val areAnimationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.ANIMATIONS_ENABLED] ?: true
        }

    /**
     * Define o estado de ativação das animações.
     *
     * @param isEnabled `true` para ativar as animações, `false` para desativar.
     */
    suspend fun setAnimationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserPreferencesKeys.ANIMATIONS_ENABLED] = isEnabled
        }
    }
}
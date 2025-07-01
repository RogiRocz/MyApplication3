package com.example.myapplication.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.UserPreferencesRepository
import com.example.myapplication.model.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val isDarkModeEnabled: StateFlow<Boolean> =
        repository.isDarkModeEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val areNotificationsEnabled: StateFlow<Boolean> =
        repository.areNotificationsEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val areAnimationsEnabled: StateFlow<Boolean> =
        repository.areAnimationsEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val uiState: StateFlow<AppSettings> = combine(
        isDarkModeEnabled,
        areNotificationsEnabled,
        areAnimationsEnabled
    ) { dark, notif, anim ->
        AppSettings(
            darkModeEnabled      = dark,
            notificationsEnabled = notif,
            animationsEnabled    = anim
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkModeEnabled(enabled)
        }
    }

    fun toggleNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    fun toggleAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAnimationsEnabled(enabled)
        }
    }
}

class SettingsViewModelFactory(
    private val repository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
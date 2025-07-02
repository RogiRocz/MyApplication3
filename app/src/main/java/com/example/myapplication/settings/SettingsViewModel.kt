package com.example.myapplication.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.UserPreferencesRepository
import com.example.myapplication.model.AppSettings // Model para o estado da UI de configurações
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de Configurações ([SettingsScreen]).
 * Gerencia o estado das preferências do usuário (modo escuro, notificações, animações)
 * e fornece métodos para atualizá-las.
 *
 * @param repository O [UserPreferencesRepository] usado para ler e escrever as preferências do usuário.
 */
class SettingsViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    /**
     * [StateFlow] que emite o estado atual da preferência de modo escuro.
     * O valor é coletado do [UserPreferencesRepository.isDarkModeEnabled].
     * `SharingStarted.WhileSubscribed(5_000)`: O Flow subjacente é iniciado quando há um coletor ativo
     * e para 5 segundos após o último coletor ser removido. Isso ajuda a economizar recursos.
     * `initialValue = false`: Valor inicial emitido até que o DataStore carregue o valor real.
     */
    val isDarkModeEnabled: StateFlow<Boolean> =
        repository.isDarkModeEnabled
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false // Valor padrão inicial
            )

    /**
     * [StateFlow] que emite o estado atual da preferência de notificações.
     * Similar ao `isDarkModeEnabled`, mas com valor inicial `true`.
     */
    val areNotificationsEnabled: StateFlow<Boolean> =
        repository.areNotificationsEnabled
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true // Valor padrão inicial
            )

    /**
     * [StateFlow] que emite o estado atual da preferência de animações.
     * Similar ao `isDarkModeEnabled`, mas com valor inicial `true`.
     */
    val areAnimationsEnabled: StateFlow<Boolean> =
        repository.areAnimationsEnabled
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true // Valor padrão inicial
            )

    /**
     * [StateFlow] que combina os estados das preferências individuais em um único objeto [AppSettings].
     * Útil se a UI precisar observar todas as configurações de uma vez ou se houver lógica
     * que dependa de múltiplas configurações.
     * `SharingStarted.Eagerly`: O Flow subjacente é iniciado imediatamente e permanece ativo
     * enquanto o `viewModelScope` estiver ativo.
     * `initialValue = AppSettings()`: Usa os valores padrão de `AppSettings` como estado inicial.
     */
    val uiState: StateFlow<AppSettings> = combine(
        isDarkModeEnabled,
        areNotificationsEnabled,
        areAnimationsEnabled
    ) { dark, notif, anim ->
        // Cria um objeto AppSettings com os valores mais recentes das preferências.
        AppSettings(
            darkModeEnabled      = dark,
            notificationsEnabled = notif,
            animationsEnabled    = anim
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // Inicia imediatamente
            initialValue = AppSettings() // Usa construtor padrão para valores iniciais
        )

    /**
     * Alterna o estado da preferência de modo escuro.
     * Lança uma coroutine no `viewModelScope` para chamar a função suspend do repositório.
     *
     * @param enabled O novo estado para o modo escuro (`true` para ativado, `false` para desativado).
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkModeEnabled(enabled)
        }
    }

    /**
     * Alterna o estado da preferência de notificações.
     *
     * @param enabled O novo estado para as notificações (`true` para ativado, `false` para desativado).
     */
    fun toggleNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    /**
     * Alterna o estado da preferência de animações.
     *
     * @param enabled O novo estado para as animações (`true` para ativado, `false` para desativado).
     */
    fun toggleAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAnimationsEnabled(enabled)
        }
    }
}

/**
 * Factory para criar instâncias de [SettingsViewModel].
 * Necessária porque o `SettingsViewModel` tem uma dependência ([UserPreferencesRepository])
 * que precisa ser injetada em seu construtor.
 *
 * @param repository A instância de [UserPreferencesRepository] a ser fornecida ao ViewModel.
 */
class SettingsViewModelFactory(
    private val repository: UserPreferencesRepository
) : ViewModelProvider.Factory { // Implementa ViewModelProvider.Factory
    /**
     * Cria uma nova instância do ViewModel solicitado.
     *
     * @param modelClass A classe do ViewModel a ser criada.
     * @return Uma instância do ViewModel.
     * @throws IllegalArgumentException Se a `modelClass` não for [SettingsViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe solicitada é SettingsViewModel ou uma subclasse.
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Suprime o aviso de cast não verificado, pois já verificamos o tipo.
            return SettingsViewModel(repository) as T // Cria e retorna a instância do ViewModel.
        }
        // Lança uma exceção se a classe do ViewModel for desconhecida.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
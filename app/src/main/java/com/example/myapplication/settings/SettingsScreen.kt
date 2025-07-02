// app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class) // Opt-in para APIs experimentais do Material 3

package com.example.myapplication.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de seta para trás com suporte a AutoMirrored
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults // Para personalizar cores da TopAppBar, se necessário
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.UserPreferencesRepository

/**
 * Composable que representa a tela de configurações do aplicativo.
 * Permite ao usuário ativar/desativar o modo escuro, notificações e animações.
 *
 * @param onBack Callback para ser invocado quando o usuário pressiona o botão de voltar,
 *               geralmente para navegar para a tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para TopAppBar e Scaffold
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // Obtém uma instância do SettingsViewModel usando a factory para injetar o UserPreferencesRepository.
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context)
        )
    )

    // Coleta os estados das preferências do ViewModel como State.
    // Isso garante que a UI seja recomposta quando esses valores mudarem.
    val darkMode by viewModel.isDarkModeEnabled.collectAsState()
    val notifications by viewModel.areNotificationsEnabled.collectAsState()
    val animations by viewModel.areAnimationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    // Botão para voltar à tela anterior.
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Ícone de voltar
                            contentDescription = "Voltar"
                        )
                    }
                },
                // Exemplo de como definir cores para a TopAppBar, se desejado.
                // colors = TopAppBarDefaults.topAppBarColors(
                // containerColor = MaterialTheme.colorScheme.primaryContainer,
                // titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                // )
            )
        }
    ) { innerPadding -> // innerPadding é fornecido pelo Scaffold para evitar sobreposição com a TopAppBar.
        Column(
            modifier = Modifier
                .padding(innerPadding) // Aplica o padding interno do Scaffold.
                .padding(16.dp) // Adiciona padding adicional ao conteúdo.
                .fillMaxSize() // Ocupa todo o espaço disponível.
        ) {
            // Linha de preferência para o Modo Escuro.
            PreferenceRow(
                title = "Modo Escuro",
                isChecked = darkMode,
                onCheckedChange = viewModel::toggleDarkMode // Referência à função do ViewModel.
            )
            Spacer(modifier = Modifier.height(16.dp)) // Espaçador vertical.

            // Linha de preferência para Notificações.
            PreferenceRow(
                title = "Notificações",
                isChecked = notifications,
                onCheckedChange = viewModel::toggleNotificationsEnabled
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Linha de preferência para Animações.
            PreferenceRow(
                title = "Animações",
                isChecked = animations,
                onCheckedChange = viewModel::toggleAnimationsEnabled
            )
        }
    }
}

/**
 * Composable que representa uma linha de preferência com um título e um Switch.
 *
 * @param title O título da preferência a ser exibido.
 * @param isChecked O estado atual do Switch (marcado ou desmarcado).
 * @param onCheckedChange Callback que é invocado quando o estado do Switch muda.
 *                        Recebe um Boolean indicando o novo estado.
 * @param modifier Modificador opcional para personalizar a aparência ou comportamento da linha.
 */
@Composable
fun PreferenceRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier // Permite que modificadores sejam passados para este Composable.
) {
    Row(
        modifier = modifier
            .fillMaxWidth() // A linha ocupa toda a largura disponível.
            .padding(vertical = 8.dp), // Padding vertical.
        verticalAlignment = Alignment.CenterVertically // Alinha os itens verticalmente ao centro.
    ) {
        // Texto do título da preferência.
        Text(
            text = title,
            modifier = Modifier.weight(1f), // Ocupa o espaço restante, empurrando o Switch para a direita.
            style = MaterialTheme.typography.bodyLarge // Estilo do texto.
        )
        // Componente Switch para ligar/desligar a preferência.
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange // Callback para mudança de estado.
        )
    }
}
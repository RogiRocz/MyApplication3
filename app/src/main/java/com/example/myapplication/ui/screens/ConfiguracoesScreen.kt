// app/src/main/java/com/example/myapplication/ui/screens/ConfiguracoesScreen.kt
package com.example.myapplication.ui.screens

import android.Manifest // Para acessar a permissão de notificação.
import android.content.pm.PackageManager // Para verificar o estado da permissão.
import android.os.Build // Para verificar a versão do SDK do Android.
import androidx.activity.compose.rememberLauncherForActivityResult // Para lançar pedidos de permissão.
import androidx.activity.result.contract.ActivityResultContracts // Contrato para pedir uma única permissão.
import androidx.core.content.ContextCompat // Para verificar permissões de forma compatível.
import androidx.compose.ui.platform.LocalContext // Para obter o contexto atual.
import android.util.Log // Para logging.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar com suporte RTL.
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch // Componente para alternar opções.
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // Barra de topo da tela.
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Para coletar StateFlow de forma segura no ciclo de vida.
import androidx.lifecycle.viewmodel.compose.viewModel // Para obter instâncias de ViewModel.
import androidx.navigation.NavHostController // Para controle de navegação.
import com.example.myapplication.model.DadosMockados // Para a ação de limpar favoritos (exemplo).
import com.example.myapplication.model.UserPreferencesRepository // Repositório de preferências do usuário.
import com.example.myapplication.settings.SettingsViewModel // ViewModel para as configurações.
import com.example.myapplication.settings.SettingsViewModelFactory // Factory para o SettingsViewModel.
import com.example.myapplication.ui.components.BottomNavigationBar // Barra de navegação inferior.
import androidx.compose.runtime.getValue // Para desempacotar o State.
// import androidx.compose.runtime.setValue // Não é usado diretamente aqui, mas útil em `remember { mutableStateOf(...) }`

/**
 * Composable que exibe a tela de Configurações do aplicativo.
 * Permite ao usuário modificar preferências como modo escuro e notificações.
 * Inclui lógica para solicitar permissão de notificação em versões apropriadas do Android.
 *
 * @param navController Controlador de navegação para permitir voltar à tela anterior.
 * @param settingsViewModel ViewModel que gerencia o estado e a lógica das configurações.
 *                          Por padrão, é obtido usando `viewModel()` com uma factory que injeta
 *                          o `UserPreferencesRepository`.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para TopAppBar e Scaffold.
@Composable
fun ConfiguracoesScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel( // Obtém o ViewModel com sua factory.
        factory = SettingsViewModelFactory(UserPreferencesRepository(LocalContext.current.applicationContext))
    )
) {
    val context = LocalContext.current // Obtém o contexto local.
    // Coleta os estados das preferências do ViewModel de forma segura em relação ao ciclo de vida.
    val modoEscuroAtivado by settingsViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    val notificacoesAtivadas by settingsViewModel.areNotificationsEnabled.collectAsStateWithLifecycle()
    // val animacoesAtivadas by settingsViewModel.areAnimationsEnabled.collectAsStateWithLifecycle() // Se for usar animações

    // Launcher para solicitar a permissão de notificação.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), // Contrato para pedir uma única permissão.
        onResult = { isGranted: Boolean -> // Callback com o resultado da solicitação.
            if (isGranted) {
                settingsViewModel.toggleNotificationsEnabled(true) // Ativa as notificações no ViewModel se concedido.
                Log.d("ConfiguracoesScreen", "Permissão de notificação concedida.")
            } else {
                settingsViewModel.toggleNotificationsEnabled(false) // Mantém desativado (ou desativa) se negado.
                Log.d("ConfiguracoesScreen", "Permissão de notificação negada.")
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    // Botão para voltar à tela anterior.
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
                // Actions (como um menu de três pontos) poderiam ser adicionadas aqui.
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra de navegação inferior.
    ) { paddingValues -> // paddingValues fornecido pelo Scaffold para evitar sobreposição.
        Column(
            modifier = Modifier
                .padding(paddingValues) // Aplica o padding do Scaffold.
                .padding(16.dp) // Padding interno adicional para o conteúdo.
                .fillMaxSize() // Ocupa todo o espaço disponível.
        ) {
            // Seção de Preferências de Exibição
            Text("Preferências de Exibição", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Opção para Modo Escuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Alinha texto à esquerda e Switch à direita.
            ) {
                Text("Modo Escuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = modoEscuroAtivado,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.toggleDarkMode(isChecked) // Atualiza o ViewModel.
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaçador entre as opções.

            // Seção de Notificações
            Text("Notificações", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Opção para Notificações
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ativar Notificações", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = notificacoesAtivadas,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // Se o usuário está tentando ATIVAR as notificações:
                            // Verifica a versão do Android e a permissão.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                                val permissionStatus = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS // Permissão específica do Android 13.
                                )
                                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                    // Permissão já concedida, apenas ativa no ViewModel.
                                    settingsViewModel.toggleNotificationsEnabled(true)
                                } else {
                                    // Permissão não concedida, solicita ao usuário.
                                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                // Para versões anteriores ao Android 13, a permissão não é necessária em tempo de execução
                                // da mesma forma (geralmente concedida na instalação ou gerenciada por canais).
                                settingsViewModel.toggleNotificationsEnabled(true)
                            }
                        } else {
                            // Se o usuário está DESATIVANDO as notificações, apenas atualiza o ViewModel.
                            settingsViewModel.toggleNotificationsEnabled(false)
                        }
                    }
                )
            }

            // Seção de Animações (se for reintroduzida)
            // Spacer(modifier = Modifier.height(16.dp))
            // Text("Outras Configurações", style = MaterialTheme.typography.titleMedium)
            // Spacer(modifier = Modifier.height(8.dp))
            // Row(
            //     modifier = Modifier.fillMaxWidth(),
            //     verticalAlignment = Alignment.CenterVertically,
            //     horizontalArrangement = Arrangement.SpaceBetween
            // ) {
            //     Text("Ativar Animações", style = MaterialTheme.typography.bodyLarge)
            //     Switch(
            //         checked = animacoesAtivadas,
            //         onCheckedChange = { settingsViewModel.toggleAnimationsEnabled(it) }
            //     )
            // }

            Spacer(modifier = Modifier.height(32.dp)) // Espaçador maior.

            // Botão de exemplo para limpar favoritos (demonstração, pode ser movido ou ter lógica real).
            Button(
                onClick = { DadosMockados.listaDeFavoritosMock.clear() }, // Ação de limpar (mockada).
                modifier = Modifier.align(Alignment.CenterHorizontally) // Centraliza o botão.
            ) {
                Text("Limpar Favoritos (Mock)")
            }
        }
    }
}
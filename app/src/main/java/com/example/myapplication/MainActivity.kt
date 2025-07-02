package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
// import androidx.compose.ui.platform.LocalContext // Import não utilizado
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.model.UserPreferencesRepository
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.settings.SettingsViewModelFactory
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.notifications.ReminderReceiver
import com.example.myapplication.ui.theme.NutriLivreTheme

/**
 * Atividade principal do aplicativo.
 * Responsável por configurar o tema, o canal de notificações (se aplicável)
 * e inicializar a navegação do aplicativo.
 */
class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var settingsViewModel: SettingsViewModel

    /**
     * Chamado quando a atividade está sendo criada.
     * Aqui é onde a maior parte da inicialização deve acontecer: chamar `setContentView(int)`
     * para inflar a UI da atividade, usar `findViewById(int)` para interagir programaticamente
     * com widgets na UI, chamar `managedQuery(android.net.Uri, String[], String, String[], String)`
     * para obter cursores para dados exibidos, etc.
     *
     * @param savedInstanceState Se a atividade estiver sendo reiniciada após ter sido
     * desligada anteriormente, este Bundle contém os dados que ela forneceu mais recentemente
     * em `onSaveInstanceState(Bundle)`. Caso contrário, é nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Criação do canal de notificação para Android Oreo (API 26) e superior.
        // Isso é necessário para que as notificações sejam exibidas corretamente nessas versões do Android.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID, // ID do canal, deve ser único por pacote.
                "Lembretes do App", // Nome do canal visível ao usuário.
                NotificationManager.IMPORTANCE_HIGH // Importância do canal.
            ).apply {
                description = "Notificações para lembretes de receitas e outras informações importantes." // Descrição do canal.
            }
            // Registra o canal no sistema.
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Inicializa o repositório de preferências do usuário.
        userPreferencesRepository = UserPreferencesRepository(applicationContext)
        // Inicializa o ViewModel para as configurações, usando uma factory para injetar o repositório.
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(userPreferencesRepository)
        )[SettingsViewModel::class.java] // Usando o operador get moderno.

        // Define o conteúdo da atividade usando Jetpack Compose.
        setContent {
            // Coleta o estado do modo escuro do settingsViewModel.
            // O valor inicial é baseado na configuração do sistema.
            val darkModeEnabled by settingsViewModel.isDarkModeEnabled.collectAsStateWithLifecycle(
                initialValue = isSystemInDarkTheme()
            )

            // Aplica o tema NutriLivre, passando o estado do modo escuro.
            NutriLivreTheme(darkTheme = darkModeEnabled) {
                // Surface é um contêiner Material Design que desenha um fundo.
                Surface(
                    modifier = Modifier.fillMaxSize(), // Ocupa todo o espaço disponível.
                    color = MaterialTheme.colorScheme.background // Cor de fundo do tema.
                ) {
                    // Configura a navegação do aplicativo, passando o settingsViewModel.
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
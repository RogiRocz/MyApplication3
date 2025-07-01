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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.model.UserPreferencesRepository
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.settings.SettingsViewModelFactory
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.notifications.ReminderReceiver
import com.example.myapplication.ui.theme.NutriLivreTheme

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                "Lembretes do App",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para lembretes de receitas e outras informações importantes."
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        userPreferencesRepository = UserPreferencesRepository(applicationContext)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(userPreferencesRepository)
        ).get(SettingsViewModel::class.java)

        setContent {
            val darkModeEnabled by settingsViewModel.isDarkModeEnabled.collectAsStateWithLifecycle(
                initialValue = isSystemInDarkTheme()
            )

            NutriLivreTheme(darkTheme = darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
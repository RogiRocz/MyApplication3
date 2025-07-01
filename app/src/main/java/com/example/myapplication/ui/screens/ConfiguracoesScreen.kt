// app/src/main/java/com/example/myapplication/ui/screens/ConfiguracoesScreen.kt
package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.model.UserPreferencesRepository
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.settings.SettingsViewModelFactory
import com.example.myapplication.ui.components.BottomNavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(UserPreferencesRepository(LocalContext.current.applicationContext))
    )
) {
    val context = LocalContext.current
    val modoEscuroAtivado by settingsViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    val notificacoesAtivadas by settingsViewModel.areNotificationsEnabled.collectAsStateWithLifecycle()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                settingsViewModel.toggleNotificationsEnabled(true)
                Log.d("ConfiguracoesScreen", "Permissão de notificação concedida.")
            } else {
                settingsViewModel.toggleNotificationsEnabled(false)
                Log.d("ConfiguracoesScreen", "Permissão de notificação negada.")
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Escuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = modoEscuroAtivado,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.toggleDarkMode(isChecked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notificações", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = notificacoesAtivadas,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionStatus = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                    settingsViewModel.toggleNotificationsEnabled(true)
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                settingsViewModel.toggleNotificationsEnabled(true)
                            }
                        } else {
                            settingsViewModel.toggleNotificationsEnabled(false)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { DadosMockados.listaDeFavoritosMock.clear() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Limpar Favoritos")
            }
        }
    }
}
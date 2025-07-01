// app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.UserPreferencesRepository

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context)
        )
    )

    val darkMode by viewModel.isDarkModeEnabled.collectAsState()
    val notifications by viewModel.areNotificationsEnabled.collectAsState()
    val animations by viewModel.areAnimationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PreferenceRow(
                title = "Modo Escuro",
                isChecked = darkMode,
                onCheckedChange = viewModel::toggleDarkMode
            )
            Spacer(modifier = Modifier.height(16.dp))

            PreferenceRow(
                title = "Notificações",
                isChecked = notifications,
                onCheckedChange = viewModel::toggleNotificationsEnabled
            )
            Spacer(modifier = Modifier.height(16.dp))

            PreferenceRow(
                title = "Animações",
                isChecked = animations,
                onCheckedChange = viewModel::toggleAnimationsEnabled
            )
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}
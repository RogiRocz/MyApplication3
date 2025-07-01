package com.example.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavHostController) {
    val context = LocalContext.current
    var assunto by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var tipoFeedback by remember { mutableStateOf("Erro") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar / Sugerir") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ajude-nos a melhorar! Reporte um erro ou sugira uma edição/nova funcionalidade.",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = tipoFeedback == "Erro",
                    onClick = { tipoFeedback = "Erro" },
                    label = { Text("Reportar Erro") }
                )
                FilterChip(
                    selected = tipoFeedback == "Sugestão",
                    onClick = { tipoFeedback = "Sugestão" },
                    label = { Text("Sugerir Edição/Funcionalidade") }
                )
            }
            OutlinedTextField(
                value = assunto,
                onValueChange = { assunto = it },
                label = { Text("Assunto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = mensagem,
                onValueChange = { mensagem = it },
                label = { Text("Sua Mensagem") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                minLines = 5
            )

            Button(
                onClick = {
                    if (assunto.isNotBlank() && mensagem.isNotBlank()) {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("seu_email@example.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "[Feedback nutriLivre - $tipoFeedback] $assunto")
                            putExtra(Intent.EXTRA_TEXT, mensagem)
                        }

                        if (emailIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(emailIntent)
                            Toast.makeText(context, "Abrindo cliente de e-mail para enviar feedback...", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Nenhum aplicativo de e-mail encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Por favor, preencha o assunto e a mensagem.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Feedback")
            }
        }
    }
}
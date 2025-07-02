package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar com suporte a RTL
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Para obter ViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens // Para navegação
import com.example.myapplication.ui.components.BottomNavigationBar // Barra de navegação inferior
import com.example.myapplication.viewmodel.BuscaViewModel // ViewModel para a lógica de busca

/**
 * Composable que representa a tela de busca de receitas.
 * Permite ao usuário digitar um termo de busca e exibe os resultados correspondentes.
 *
 * @param navController Controlador de navegação para permitir a transição para outras telas (ex: detalhes da receita).
 * @param buscaViewModel ViewModel que contém a lógica de busca e o estado da tela de busca.
 *                       É obtido usando `viewModel()` por padrão, o que o associa ao ciclo de vida correto.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para componentes como TopAppBar, Scaffold, OutlinedTextField
@Composable
fun BuscaScreen(
    navController: NavHostController,
    buscaViewModel: BuscaViewModel = viewModel() // Injeta o ViewModel de busca
) {
    // Coleta os estados do ViewModel para que a UI seja recomposta quando eles mudarem.
    val searchText by buscaViewModel.searchText.collectAsState()
    val searchResults by buscaViewModel.searchResults.collectAsState()
    val isLoading by buscaViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Receitas") },
                navigationIcon = {
                    // Botão para voltar à tela anterior
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra de navegação inferior
    ) { paddingValues -> // paddingValues fornecido pelo Scaffold para evitar sobreposição com barras
        Column(
            modifier = Modifier
                .padding(paddingValues) // Aplica o padding do Scaffold
                .fillMaxSize() // Ocupa todo o espaço disponível
                .padding(16.dp), // Padding interno para o conteúdo
            horizontalAlignment = Alignment.CenterHorizontally // Centraliza o conteúdo horizontalmente
        ) {
            // Campo de texto para o usuário digitar a busca
            OutlinedTextField(
                value = searchText,
                onValueChange = { buscaViewModel.onSearchTextChanged(it) }, // Atualiza o ViewModel quando o texto muda
                label = { Text("Pesquisar receitas...") },
                singleLine = true, // O campo de texto terá apenas uma linha
                modifier = Modifier.fillMaxWidth(), // Ocupa toda a largura
                leadingIcon = { // Ícone à esquerda do campo de texto
                    Icon(Icons.Filled.Search, contentDescription = "Ícone de busca")
                }
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espaçador vertical

            // Lógica para exibir o indicador de carregamento, resultados ou mensagens de estado
            if (isLoading) {
                // Exibe um indicador de progresso circular e um texto enquanto carrega
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Buscando receitas...", style = MaterialTheme.typography.bodyMedium)
            } else {
                // Se não estiver carregando, verifica o estado da busca
                if (searchText.isNotBlank() && searchResults.isEmpty()) {
                    // Se houver texto de busca mas nenhum resultado
                    Text("Nenhum resultado encontrado para \"$searchText\".",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else if (searchText.isBlank()) {
                    // Se o campo de busca estiver vazio
                    Text("Digite para buscar receitas.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    // Se houver resultados, exibe-os em uma LazyColumn
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os itens
                    ) {
                        items(searchResults) { receita -> // Itera sobre a lista de resultados
                            // Exibe um card para cada receita (ReceitaCard deve estar definido em outro lugar)
                            ReceitaCard( // Presume-se que ReceitaCard é um Composable definido
                                receita = receita,
                                onClick = {
                                    // Navega para a tela de detalhes da receita ao clicar no card
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

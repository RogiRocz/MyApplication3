package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.BuscaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaScreen(
    navController: NavHostController,
    buscaViewModel: BuscaViewModel = viewModel()
) {
    val searchText by buscaViewModel.searchText.collectAsState()
    val searchResults by buscaViewModel.searchResults.collectAsState()
    val isLoading by buscaViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Receitas") },
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
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { buscaViewModel.onSearchTextChanged(it) },
                label = { Text("Pesquisar receitas...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Ícone de busca")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Buscando receitas...", style = MaterialTheme.typography.bodyMedium)
            } else {
                if (searchText.isNotBlank() && searchResults.isEmpty()) {
                    Text("Nenhum resultado encontrado para \"$searchText\".",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else if (searchText.isBlank()) {
                    Text("Digite para buscar receitas.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { receita ->
                            ReceitaCard(
                                receita = receita,
                                onClick = {
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

// **IMPORTANTE**: Você precisa ter um `ReceitaCard` ou similar definido em outro lugar.
// Se você não tiver, um exemplo simples:
/*
@Composable
fun ReceitaCard(receita: Receita, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Se tiver imagem na receita:
            // AsyncImage(
            //     model = receita.imagemUrl,
            //     contentDescription = receita.nome,
            //     modifier = Modifier
            //         .size(64.dp)
            //         .clip(MaterialTheme.shapes.small),
            //     contentScale = ContentScale.Crop
            // )
            // Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(receita.nome, style = MaterialTheme.typography.titleMedium)
                Text(receita.descricaoCurta, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
*/
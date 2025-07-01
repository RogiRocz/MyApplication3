package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.model.ListaComprasItem
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.ListaComprasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaComprasScreen(
    navController: NavHostController,
    listaComprasViewModel: ListaComprasViewModel = viewModel()
) {
    val shoppingList by listaComprasViewModel.listaCompras.collectAsState()
    val selectedItems by listaComprasViewModel.selectedItems.collectAsState()
    val isMultiSelectionMode by listaComprasViewModel.isMultiSelectionMode.collectAsState()
    val progress by listaComprasViewModel.progress.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lista de Compras")
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                navigationIcon = {
                    if (isMultiSelectionMode) {
                        IconButton(onClick = { listaComprasViewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar Seleção")
                        }
                    } else {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                actions = {
                    if (isMultiSelectionMode) {
                        IconButton(
                            onClick = { listaComprasViewModel.deleteSelectedItems() },
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir Selecionados")
                        }
                    } else {
                        IconButton(onClick = { listaComprasViewModel.toggleAllItemsBoughtStatus() }) {
                            Icon(painterResource(id = R.drawable.done_all_24px), contentDescription = "Marcar/Desmarcar Todos")
                        }
                        IconButton(onClick = { listaComprasViewModel.clearShoppingList() }) {
                            Icon(painterResource(id = R.drawable.clear_all_24px), contentDescription = "Limpar Lista")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        if (shoppingList.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Lista Vazia",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Sua lista de compras está vazia!\nAdicione algo para começar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(shoppingList, key = { it.id }) { item ->
                    ShoppingListItemComposable(
                        item = item,
                        isSelected = selectedItems.contains(item.id),
                        isMultiSelectionMode = isMultiSelectionMode,
                        onToggleBought = { listaComprasViewModel.toggleBoughtStatus(item.id) },
                        onDeleteItem = { listaComprasViewModel.deleteItem(item.id) },
                        onLongClick = { listaComprasViewModel.toggleSelection(item.id) },
                        onClick = {
                            if (isMultiSelectionMode) {
                                listaComprasViewModel.toggleSelection(item.id)
                            }
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Adicionar Item") },
            text = {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Nome do Item") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            listaComprasViewModel.addItem(newItemName)
                            newItemName = ""
                            showAddItemDialog = false
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ShoppingListItemComposable(
    item: ListaComprasItem,
    isSelected: Boolean,
    isMultiSelectionMode: Boolean,
    onToggleBought: () -> Unit,
    onDeleteItem: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick)
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if (isMultiSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() }
                )
                Spacer(Modifier.width(8.dp))
            }

            IconButton(onClick = onToggleBought) {
                Icon(
                    imageVector = if (item.isBought) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                    contentDescription = if (item.isBought) "Desmarcar como Comprado" else "Marcar como Comprado",
                    tint = if (item.isBought) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isBought) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )


            if (!isMultiSelectionMode) {
                IconButton(onClick = onDeleteItem) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
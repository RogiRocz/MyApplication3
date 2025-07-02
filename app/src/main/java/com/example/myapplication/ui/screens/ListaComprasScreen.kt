package com.example.myapplication.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable // Para tornar os itens clicáveis
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar com suporte RTL
import androidx.compose.material.icons.filled.* // Importa todos os ícones preenchidos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color // Não utilizado diretamente, mas pode ser útil para customizações
import androidx.compose.ui.res.painterResource // Para carregar drawables como painters
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Para obter o ViewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ui.components.BottomNavigationBar // Barra de navegação inferior
import com.example.myapplication.viewmodel.ListaComprasViewModel // ViewModel da lista de compras
import com.example.myapplication.model.ListaComprasItem // Modelo do item da lista
import com.example.myapplication.R // Para acessar recursos (drawables)

/**
 * Composable que exibe a tela da lista de compras.
 * Permite ao usuário adicionar, remover, marcar/desmarcar itens e limpar a lista.
 * Também suporta seleção múltipla para exclusão de itens.
 *
 * @param navController Controlador de navegação para voltar à tela anterior.
 * @param listaComprasViewModel ViewModel que gerencia o estado e a lógica da lista de compras.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para componentes Material 3
@Composable
fun ListaComprasScreen(
    navController: NavHostController,
    listaComprasViewModel: ListaComprasViewModel = viewModel() // Injeta o ViewModel
) {
    // Coleta os estados do ViewModel para reatividade da UI.
    val listaCompras by listaComprasViewModel.listaCompras.collectAsState()
    val selectedItems by listaComprasViewModel.selectedItems.collectAsState()
    val isMultiSelectionMode by listaComprasViewModel.isMultiSelectionMode.collectAsState()
    val rawProgress by listaComprasViewModel.progress.collectAsState() // Progresso bruto (0.0 a 1.0)

    // Anima a barra de progresso para uma transição suave.
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 500), label = "ProgressAnimation" // Especifica a animação e uma label
    )

    // Estados para controlar o diálogo de adicionar item.
    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lista de Compras")
                        Spacer(modifier = Modifier.height(4.dp))
                        // Barra de progresso linear que mostra a porcentagem de itens comprados.
                        LinearProgressIndicator(
                            progress = { animatedProgress }, // Usa o progresso animado
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                navigationIcon = {
                    // Ícone de navegação: "Fechar" no modo de seleção múltipla, "Voltar" caso contrário.
                    if (isMultiSelectionMode) {
                        IconButton(onClick = { listaComprasViewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar Seleção")
                        }
                    } else {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                actions = {
                    // Ações da TopAppBar: "Excluir" no modo de seleção, "Marcar Todos" e "Limpar Lista" caso contrário.
                    if (isMultiSelectionMode) {
                        IconButton(
                            onClick = { listaComprasViewModel.deleteSelectedItems() },
                            enabled = selectedItems.isNotEmpty() // Habilitado apenas se houver itens selecionados.
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir Selecionados")
                        }
                    } else {
                        // Ícone para marcar/desmarcar todos os itens como comprados.
                        IconButton(onClick = { listaComprasViewModel.toggleAllItemsBoughtStatus() }) {
                            Icon(painterResource(id = R.drawable.done_all_24px), contentDescription = "Marcar/Desmarcar Todos")
                        }
                        // Ícone para limpar toda a lista de compras.
                        IconButton(onClick = { listaComprasViewModel.clearShoppingList() }) {
                            Icon(painterResource(id = R.drawable.clear_all_24px), contentDescription = "Limpar Lista")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão de Ação Flutuante para adicionar um novo item à lista.
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra de navegação inferior.
    ) { paddingValues -> // paddingValues do Scaffold para evitar sobreposição.
        // Conteúdo principal da tela.
        if (listaCompras.isEmpty()) {
            // Exibe uma mensagem se a lista estiver vazia.
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCartCheckout, // Ícone de carrinho de compras.
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
            // Exibe os itens da lista em uma LazyColumn se a lista não estiver vazia.
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp), // Padding horizontal para os itens.
                contentPadding = PaddingValues(bottom = 16.dp) // Padding na parte inferior da lista.
            ) {
                items(listaCompras, key = { it.id }) { item -> // Usa o ID do item como chave para melhor performance.
                    ListaComprasItemComposable(
                        item = item,
                        isSelected = selectedItems.contains(item.id),
                        isMultiSelectionMode = isMultiSelectionMode,
                        onToggleBought = { listaComprasViewModel.toggleBoughtStatus(item.id) },
                        onDeleteItem = { listaComprasViewModel.deleteItem(item.id) },
                        onLongClick = { listaComprasViewModel.toggleSelection(item.id) }, // Habilita seleção múltipla com clique longo.
                        onClick = {
                            if (isMultiSelectionMode) {
                                listaComprasViewModel.toggleSelection(item.id) // Alterna seleção no modo de seleção múltipla.
                            }
                            // Poderia adicionar outra ação para clique simples se não estiver em modo de seleção.
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) // Divisor entre os itens.
                }
            }
        }
    }

    // Diálogo para adicionar um novo item.
    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false }, // Fecha o diálogo ao clicar fora ou no botão de voltar.
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
                        if (newItemName.isNotBlank()) { // Adiciona o item apenas se o nome não estiver vazio.
                            listaComprasViewModel.addItem(newItemName)
                            newItemName = "" // Limpa o campo após adicionar.
                            showAddItemDialog = false // Fecha o diálogo.
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

/**
 * Composable que representa um único item na lista de compras.
 * Exibe o nome do item, um botão para marcar/desmarcar como comprado, e um botão para excluir.
 * Suporta destaque visual quando selecionado no modo de seleção múltipla.
 *
 * @param item O objeto [ListaComprasItem] a ser exibido.
 * @param isSelected Indica se o item está atualmente selecionado no modo de seleção múltipla.
 * @param isMultiSelectionMode Indica se o modo de seleção múltipla está ativo.
 * @param onToggleBought Callback para ser invocado quando o status de "comprado" do item é alternado.
 * @param onDeleteItem Callback para ser invocado quando o item é excluído.
 * @param onLongClick Callback para ser invocado quando o item recebe um clique longo (para iniciar a seleção múltipla).
 * @param onClick Callback para ser invocado quando o item recebe um clique simples.
 */
@Composable
fun ListaComprasItemComposable(
    item: ListaComprasItem,
    isSelected: Boolean,
    isMultiSelectionMode: Boolean,
    onToggleBought: () -> Unit,
    onDeleteItem: () -> Unit,
    onLongClick: () -> Unit, // Adicionado para lidar com clique longo
    onClick: () -> Unit      // Adicionado para lidar com clique normal (usado na seleção múltipla)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(
                onClick = onClick, // Ação de clique normal
                onLongClick = onLongClick // Ação de clique longo
            )
            .height(60.dp), // Altura fixa para o card do item.
        colors = CardDefaults.cardColors(
            // Cor de fundo diferente se o item estiver selecionado.
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Espaça os elementos internos.
        ) {
            // Checkbox visível apenas no modo de seleção múltipla.
            if (isMultiSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() } // Usa o onClick para alternar a seleção.
                )
                Spacer(Modifier.width(8.dp)) // Espaçador após o checkbox.
            }

            // Botão para marcar/desmarcar o item como comprado.
            IconButton(onClick = onToggleBought) {
                Icon(
                    imageVector = if (item.isBought) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (item.isBought) "Desmarcar como Comprado" else "Marcar como Comprado",
                    tint = if (item.isBought) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Nome do item.
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                // Cor do texto diferente se o item já foi comprado.
                color = if (item.isBought) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f) // Ocupa o espaço restante.
            )

            // Botão para excluir o item, visível apenas se não estiver no modo de seleção múltipla.
            if (!isMultiSelectionMode) {
                IconButton(onClick = onDeleteItem) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Excluir Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
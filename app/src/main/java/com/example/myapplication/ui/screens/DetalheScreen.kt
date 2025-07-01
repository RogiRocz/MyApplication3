package com.example.myapplication.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.model.Receita
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.ListaComprasViewModel
import java.util.Locale
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(navController: NavHostController, receitaId: Int?, listaComprasViewModel: ListaComprasViewModel) {
    val receita = remember { DadosMockados.listaDeReceitas.find { it.id == receitaId } }
    var isFavorite by remember { mutableStateOf(receita?.isFavorita ?: false) }
    var expandedMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val listener = OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("pt", "BR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "Idioma não suportado para TTS.", Toast.LENGTH_LONG).show()
                    isTtsReady = false
                } else {
                    isTtsReady = true
                }
            } else {
                Toast.makeText(context, "Erro ao inicializar o Text-to-Speech.", Toast.LENGTH_LONG).show()
                isTtsReady = false
            }
        }
        textToSpeech = TextToSpeech(context, listener)

        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    val relatedRecipes = remember(receita) {
        receita?.let { current ->
            getRelatedRecipes(current, DadosMockados.listaDeReceitas)
        } ?: emptyList()
    }

    val currentShoppingListItems by listaComprasViewModel.listaCompras.collectAsState()

    val allIngredientsAddedToShoppingList by remember(receita, currentShoppingListItems) {
        mutableStateOf(
            receita?.ingredientes?.all { ingrediente ->
                currentShoppingListItems.any { it.name.equals(ingrediente.trim(), ignoreCase = true) }
            } ?: false
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(receita?.nome ?: "Detalhe") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                IconButton(onClick = { expandedMenu = true}) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = expandedMenu, onDismissRequest = {expandedMenu = false}) {
                    DropdownMenuItem(
                        text = {Text("Compartilhar")},
                        onClick = {
                            expandedMenu = false
                            receita?.let { r ->
                                val shareText = """
                                    Confira a receita de ${r.nome} no nutriLivre!

                                    Ingredientes:
                                    ${r.ingredientes.joinToString("\n") { "- $it" }}

                                    Modo de Preparo:
                                    ${r.modoPreparo.mapIndexed { index, passo -> "${index + 1}. $passo" }.joinToString("\n")}

                                    Baixe o app nutriLivre para mais receitas!
                                """.trimIndent()

                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Adicionar a lista de compras")},
                        onClick = {
                            receita?.ingredientes?.let {
                                listaComprasViewModel.addItems(it)
                                Toast.makeText(context, "Ingredientes adicionados!", Toast.LENGTH_SHORT).show()
                            }
                            expandedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Reportar erro / Sugerir edição")},
                        onClick = {
                            expandedMenu = false
                            navController.navigate(AppScreens.FeedbackScreen.route)
                        }
                    )
                }
            })
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        receita?.let { r ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                AsyncImage(
                    model = r.imagemUrl,
                    contentDescription = r.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = r.nome, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = r.descricaoCurta, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (allIngredientsAddedToShoppingList) {
                            receita.ingredientes.forEach { ingrediente ->
                                val itemToRemove = currentShoppingListItems.find { it.name.equals(ingrediente, ignoreCase = true) }
                                itemToRemove?.let {
                                    listaComprasViewModel.deleteItem(it.id)
                                }
                            }
                            Toast.makeText(context, "Todos os ingredientes removidos da lista!", Toast.LENGTH_SHORT).show()
                        } else {
                            listaComprasViewModel.addItems(receita.ingredientes)
                            Toast.makeText(context, "Todos os ingredientes adicionados!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val icon = if (allIngredientsAddedToShoppingList) Icons.Default.Check else Icons.Default.Add
                    val text = if (allIngredientsAddedToShoppingList) "Remover todos da Lista de Compras" else "Adicionar todos à Lista de Compras"
                    Icon(icon, contentDescription = text)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleLarge)
                r.ingredientes.forEach { ingrediente ->
                    val isIngredientInShoppingList by remember(ingrediente, currentShoppingListItems) {
                        mutableStateOf(currentShoppingListItems.any { it.name.equals(ingrediente, ignoreCase = true) })
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ingrediente,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            if (isIngredientInShoppingList) {
                                val itemToRemove = currentShoppingListItems.find { it.name.equals(ingrediente, ignoreCase = true) }
                                itemToRemove?.let {
                                    listaComprasViewModel.deleteItem(it.id)
                                    Toast.makeText(context, "$ingrediente removido!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                listaComprasViewModel.addItem(ingrediente)
                                Toast.makeText(context, "$ingrediente adicionado!", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            val icon = if (isIngredientInShoppingList) Icons.Default.Check else Icons.Default.Add
                            val contentDescription = if (isIngredientInShoppingList) "Remover $ingrediente da lista" else "Adicionar $ingrediente à lista"
                            Icon(icon, contentDescription = contentDescription)
                        }
                    }
                    Divider()
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleLarge)
                r.modoPreparo.forEachIndexed { index, passo ->
                    Text(text = "${index + 1}. $passo")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        r.isFavorita = !r.isFavorita
                        isFavorite = r.isFavorita
                        if (isFavorite) {
                            DadosMockados.listaDeFavoritosMock.add(r)
                        } else {
                            DadosMockados.listaDeFavoritosMock.remove(r)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.surface else LocalContentColor.current
                        )
                        Text(if (isFavorite) " Remover dos Favoritos" else " Adicionar aos Favoritos")
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val ingredientesTexto = "Ingredientes: " + r.ingredientes.joinToString(separator = ". ") { it.trimStart('-').trim() } + "."
                        val modoPreparoTexto = "Modo de preparo: " + r.modoPreparo.joinToString(separator = ". ") { it.trimStart(*"0123456789.".toCharArray()).trim() } + "."

                        val textoCompleto = "$ingredientesTexto $modoPreparoTexto"

                        if (isTtsReady) {
                            textToSpeech?.speak(textoCompleto, TextToSpeech.QUEUE_FLUSH, null, "")
                        } else {
                            Toast.makeText(context, "Text-to-Speech não está pronto.", Toast.LENGTH_SHORT).show()
                        }
                    },
                        enabled = isTtsReady
                    ) {
                        Text("Ouvir")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (relatedRecipes.isNotEmpty()) {
                    Text("Receitas Relacionadas:", style = MaterialTheme.typography.titleLarge)
                    LazyRow {
                        items(relatedRecipes) { relatedReceita ->
                            Card(
                                modifier = Modifier
                                    .width(150.dp)
                                    .padding(end = 8.dp),
                                onClick = {
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(relatedReceita.id))
                                }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    AsyncImage(
                                        model = relatedReceita.imagemUrl,
                                        contentDescription = relatedReceita.nome,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(relatedReceita.nome, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                } else {
                    Text("Não há receitas relacionadas disponíveis.", style = MaterialTheme.typography.labelSmall)
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Receita não encontrada.", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

fun getRelatedRecipes(currentRecipe: Receita, allRecipes: List<Receita>): List<Receita> {
    val otherRecipes = allRecipes.filter { it.id != currentRecipe.id }

    val currentKeywords = (currentRecipe.ingredientes.map { it.lowercase() } +
            currentRecipe.nome.lowercase().split(" "))
        .flatMap { it.split(" ", ",") }
        .filter { it.isNotBlank() && it.length > 2 }
        .toSet()

    if (currentKeywords.isEmpty()) {
        return otherRecipes.shuffled().take(3)
    }

    val relatedRecipes = mutableListOf<Pair<Receita, Int>>()

    for (otherRecipe in otherRecipes) {
        val otherKeywords = (otherRecipe.ingredientes.map { it.lowercase() } +
                otherRecipe.nome.lowercase(Locale.ROOT).split(" "))
            .flatMap { it.split(" ", ",") }
            .filter { it.isNotBlank() && it.length > 2 }
            .toSet()

        val commonWordsCount = currentKeywords.intersect(otherKeywords).size

        if (commonWordsCount > 0) {
            relatedRecipes.add(Pair(otherRecipe, commonWordsCount))
        }
    }

    return relatedRecipes.sortedByDescending { it.second }
        .map { it.first }
        .take(3)
}
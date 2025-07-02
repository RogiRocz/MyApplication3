package com.example.myapplication.ui.screens

import android.content.Intent // Para compartilhar texto.
import android.speech.tts.TextToSpeech // Para funcionalidade Text-to-Speech.
import android.speech.tts.TextToSpeech.OnInitListener // Listener para inicialização do TTS.
import android.widget.Toast // Para exibir mensagens curtas ao usuário.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow // Para exibir receitas relacionadas horizontalmente.
import androidx.compose.foundation.lazy.items // Para popular LazyRow.
import androidx.compose.foundation.rememberScrollState // Para lembrar o estado de rolagem.
import androidx.compose.foundation.verticalScroll // Para permitir rolagem vertical.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar com suporte RTL.
import androidx.compose.material.icons.filled.Add // Ícone de adicionar.
import androidx.compose.material.icons.filled.Check // Ícone de verificado.
import androidx.compose.material.icons.filled.Favorite // Ícone de favorito preenchido.
import androidx.compose.material.icons.filled.FavoriteBorder // Ícone de favorito (borda).
import androidx.compose.material.icons.filled.MoreVert // Ícone de "mais opções" (menu).
import androidx.compose.material.icons.filled.VolumeUp // Ícone para ouvir (TTS).
// import androidx.compose.material.icons.filled.Share // Ícone de compartilhar (usado implicitamente pelo Intent).
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Para acessar o contexto (necessário para Toast, Intent, TTS).
import androidx.compose.ui.text.font.FontWeight // Para estilizar texto
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Para line height
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // Para carregar imagens de forma assíncrona (de URLs ou recursos).
import com.example.myapplication.model.DadosMockados // Fonte dos dados mockados.
import com.example.myapplication.model.Receita // Modelo de dados da Receita.
import com.example.myapplication.navigation.AppScreens // Definições das rotas do app.
import com.example.myapplication.ui.components.BottomNavigationBar // Barra de navegação inferior.
import com.example.myapplication.viewmodel.ListaComprasViewModel // ViewModel para a lista de compras.
import java.util.Locale // Para definir o idioma do TTS.
// import kotlin.collections.isNotEmpty // Usado implicitamente.

/**
 * Composable que exibe a tela de detalhes de uma receita específica.
 * Mostra a imagem, nome, descrição, ingredientes, modo de preparo, e permite favoritar,
 * adicionar ingredientes à lista de compras, compartilhar, ouvir a receita (TTS) e
 * ver receitas relacionadas.
 *
 * @param navController Controlador de navegação para permitir voltar ou navegar para outras telas.
 * @param receitaId O ID da receita a ser exibida. Pode ser nulo se a rota não fornecer um ID válido.
 * @param listaComprasViewModel ViewModel para interagir com a lista de compras.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para componentes Material 3.
@Composable
fun DetalheScreen(
    navController: NavHostController,
    receitaId: Int?, // O ID da receita agora pode ser nulo.
    listaComprasViewModel: ListaComprasViewModel
) {
    // Encontra a receita na lista de mock de dados. `remember` com `receitaId` como chave
    // garante que a busca só ocorra quando `receitaId` mudar.
    val receita = remember(receitaId) { DadosMockados.listaDeReceitas.find { it.id == receitaId } }

    // Estados da UI
    // `remember(receita)` faz com que `isFavorite` seja reavaliado se `receita` mudar.
    var isFavorite by remember(receita) { mutableStateOf(receita?.isFavorita ?: false) }
    var expandedMenu by remember { mutableStateOf(false) } // Controla a visibilidade do menu dropdown.
    val scrollState = rememberScrollState() // Estado para a rolagem vertical da tela.
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) } // Instância do TextToSpeech.
    var isTtsReady by remember { mutableStateOf(false) } // Indica se o TTS está pronto para uso.
    val context = LocalContext.current // Contexto local para Toasts, Intents, TTS.

    // Efeito para inicializar e liberar o TextToSpeech.
    // `DisposableEffect` garante que o TTS seja desligado (`shutdown`) quando o Composable é removido da tela.
    DisposableEffect(Unit) { // `Unit` como chave significa que o efeito roda uma vez na composição inicial.
        val listener = OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("pt", "BR")) // Define o idioma para Português do Brasil.
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "Idioma (PT-BR) não suportado para TTS.", Toast.LENGTH_LONG).show()
                    isTtsReady = false
                } else {
                    isTtsReady = true // TTS pronto.
                }
            } else {
                Toast.makeText(context, "Erro ao inicializar o Text-to-Speech.", Toast.LENGTH_LONG).show()
                isTtsReady = false
            }
        }
        textToSpeech = TextToSpeech(context, listener) // Cria a instância do TTS.

        onDispose { // Chamado quando o Composable é descartado.
            textToSpeech?.stop()
            textToSpeech?.shutdown() // Libera os recursos do TTS.
        }
    }

    // Encontra receitas relacionadas (mock). `remember` com `receita` como chave.
    val relatedRecipes = remember(receita) {
        receita?.let { current ->
            getRelatedRecipes(current, DadosMockados.listaDeReceitas)
        } ?: emptyList() // Retorna lista vazia se a receita principal for nula.
    }

    // Observa a lista de compras atual do ViewModel.
    val currentShoppingListItems by listaComprasViewModel.listaCompras.collectAsState()

    // Determina se todos os ingredientes da receita atual já foram adicionados à lista de compras.
    // `remember` com chaves `receita` e `currentShoppingListItems` para recalcular quando eles mudam.
    val allIngredientsAddedToShoppingList by remember(receita, currentShoppingListItems) {
        mutableStateOf(
            receita?.ingredientes?.all { ingrediente -> // Verifica se TODOS os ingredientes...
                currentShoppingListItems.any { it.name.equals(ingrediente.trim(), ignoreCase = true) } // ...existem na lista de compras.
            } ?: false // Se a receita for nula, considera que não foram adicionados.
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(receita?.nome ?: "Detalhes da Receita", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) }, // Título dinâmico e seguro contra nulo.
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Botão para voltar.
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = { // Ações na TopAppBar (menu).
                    IconButton(onClick = { expandedMenu = true }) { // Abre o menu dropdown.
                        Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                    }
                    // Menu dropdown com opções de compartilhar, adicionar à lista, reportar erro.
                    DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Compartilhar Receita") },
                            onClick = {
                                expandedMenu = false
                                receita?.let { r ->
                                    // Cria o texto de compartilhamento formatado.
                                    val shareText = """
                                        Confira esta receita deliciosa: ${r.nome}!
                                        Ingredientes:
                                        ${r.ingredientes.joinToString("\n") { "- $it" }}

                                        Modo de Preparo:
                                        ${r.modoPreparo.mapIndexed { index, passo -> "${index + 1}. $passo" }.joinToString("\n")}

                                        Enviado pelo App NutriLivre.
                                    """.trimIndent()
                                    // Cria um Intent para compartilhar o texto.
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Compartilhar receita via")
                                    context.startActivity(shareIntent) // Inicia a atividade de compartilhamento.
                                } ?: Toast.makeText(context, "Não é possível compartilhar uma receita nula.", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Adicionar todos os ingredientes à lista") },
                            onClick = {
                                receita?.ingredientes?.let {
                                    if (it.isNotEmpty()) {
                                        listaComprasViewModel.addItems(it) // Adiciona todos os ingredientes.
                                        Toast.makeText(context, "Todos os ingredientes foram adicionados à lista!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Não há ingredientes para adicionar.", Toast.LENGTH_SHORT).show()
                                    }
                                } ?: Toast.makeText(context, "Receita não encontrada para adicionar ingredientes.", Toast.LENGTH_SHORT).show()
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reportar Erro / Sugerir Edição") },
                            onClick = {
                                expandedMenu = false
                                navController.navigate(AppScreens.FeedbackScreen.route) // Navega para a tela de feedback.
                            }
                        )
                    }
                })
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra de navegação inferior.
    ) { paddingValues ->
        // Verifica se a receita foi encontrada.
        receita?.let { r -> // Usa 'r' como alias para a receita dentro deste bloco.
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Aplica padding do Scaffold.
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Padding para o conteúdo.
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Habilita rolagem vertical.
            ) {
                // Imagem da receita.
                AsyncImage(
                    model = r.imagemUrl, // URL ou ID do recurso da imagem.
                    contentDescription = "Imagem da receita ${r.nome}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Altura da imagem.
                        .padding(bottom = 8.dp) // Espaço abaixo da imagem
                )
                // Nome e descrição da receita.
                Text(text = r.nome, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = r.descricaoCurta, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Botão para adicionar/remover todos os ingredientes da lista de compras.
                Button(
                    onClick = {
                        if (allIngredientsAddedToShoppingList) {
                            // Se todos já estão na lista, remove todos.
                            r.ingredientes.forEach { ingrediente ->
                                val itemToRemove = currentShoppingListItems.find { it.name.equals(ingrediente.trim(), ignoreCase = true) }
                                itemToRemove?.let { listaComprasViewModel.deleteItem(it.id) }
                            }
                            Toast.makeText(context, "Ingredientes removidos da lista!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Caso contrário, adiciona todos.
                            if (r.ingredientes.isNotEmpty()) {
                                listaComprasViewModel.addItems(r.ingredientes)
                                Toast.makeText(context, "Ingredientes adicionados à lista!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Não há ingredientes para adicionar.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = r.ingredientes.isNotEmpty() // Desabilita se não houver ingredientes.
                ) {
                    val icon = if (allIngredientsAddedToShoppingList) Icons.Default.Check else Icons.Default.Add
                    val buttonText = if (allIngredientsAddedToShoppingList) "Remover Todos da Lista" else "Adicionar Todos à Lista"
                    Icon(icon, contentDescription = buttonText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(buttonText)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Seção de Ingredientes.
                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                if (r.ingredientes.isNotEmpty()) {
                    r.ingredientes.forEach { ingrediente ->
                        // Verifica se este ingrediente específico já está na lista de compras.
                        val isIngredientInShoppingList by remember(ingrediente, currentShoppingListItems) {
                            mutableStateOf(currentShoppingListItems.any { it.name.equals(ingrediente.trim(), ignoreCase = true) })
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp), // Padding menor para cada item
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• $ingrediente", // Marcador de lista.
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // Ocupa o espaço restante.
                            )
                            // Botão para adicionar/remover o ingrediente individualmente.
                            IconButton(
                                onClick = {
                                    if (isIngredientInShoppingList) {
                                        val itemToRemove = currentShoppingListItems.find { it.name.equals(ingrediente.trim(), ignoreCase = true) }
                                        itemToRemove?.let {
                                            listaComprasViewModel.deleteItem(it.id)
                                            Toast.makeText(context, "'$ingrediente' removido da lista.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        listaComprasViewModel.addItem(ingrediente)
                                        Toast.makeText(context, "'$ingrediente' adicionado à lista.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(36.dp) // Tamanho do IconButton
                            ) {
                                val icon = if (isIngredientInShoppingList) Icons.Default.Check else Icons.Default.Add
                                val contentDescription = if (isIngredientInShoppingList) "Remover $ingrediente" else "Adicionar $ingrediente"
                                Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant) // Divisor sutil.
                    }
                } else {
                    Text("Nenhum ingrediente listado.", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Seção de Modo de Preparo.
                Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                if (r.modoPreparo.isNotEmpty()) {
                    r.modoPreparo.forEachIndexed { index, passo ->
                        Text(
                            text = "${index + 1}. $passo", // Numera os passos.
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp, // Ajusta espaçamento entre linhas
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                } else {
                    Text("Modo de preparo não disponível.", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botões de Ação: Favoritar e Ouvir (TTS).
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os botões.
                ) {
                    // Botão de Favoritar.
                    OutlinedButton( // Mudado para OutlinedButton para diferenciar
                        onClick = {
                            r.isFavorita = !r.isFavorita // Alterna o estado no objeto mockado.
                            isFavorite = r.isFavorita // Atualiza o estado local da UI.
                            if (isFavorite) DadosMockados.listaDeFavoritosMock.add(r) else DadosMockados.listaDeFavoritosMock.remove(r)
                            val feedbackMsg = if (isFavorite) "${r.nome} adicionada aos favoritos!" else "${r.nome} removida dos favoritos."
                            Toast.makeText(context, feedbackMsg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f) // Ocupa metade do espaço.
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary // Cor de erro para favorito
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isFavorite) "Favoritado" else "Favoritar")
                    }
                    // Botão para Ouvir a receita (Text-to-Speech).
                    Button(
                        onClick = {
                            // Constrói o texto completo da receita para o TTS.
                            val ingredientesTexto = "Ingredientes: " + r.ingredientes.joinToString(separator = ". ") { it.trimStart('-').trim() } + "."
                            val modoPreparoTexto = "Modo de preparo: " + r.modoPreparo.joinToString(separator = ". ") { it.trimStart(*"0123456789.".toCharArray()).trim() } + "."
                            val textoCompleto = "${r.nome}. $ingredientesTexto $modoPreparoTexto"

                            if (isTtsReady) {
                                textToSpeech?.speak(textoCompleto, TextToSpeech.QUEUE_FLUSH, null, null) // Fala o texto.
                            } else {
                                Toast.makeText(context, "Aguarde, o recurso de leitura está inicializando.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = isTtsReady, // Habilitado apenas se o TTS estiver pronto.
                        modifier = Modifier.weight(1f) // Ocupa metade do espaço.
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Ouvir receita")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ouvir Receita")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Seção de Receitas Relacionadas.
                if (relatedRecipes.isNotEmpty()) {
                    Text("Você também pode gostar:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Exibe horizontalmente.
                        items(relatedRecipes) { relatedReceita ->
                            // Card para cada receita relacionada.
                            ElevatedCard( // Usando ElevatedCard para mais destaque
                                modifier = Modifier
                                    .width(180.dp), // Largura do card.
                                onClick = {
                                    // Navega para os detalhes da receita relacionada ao clicar.
                                    // Garante que o ID não seja nulo antes de criar a rota.
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(relatedReceita.id))
                                }
                            ) {
                                Column {
                                    AsyncImage(
                                        model = relatedReceita.imagemUrl,
                                        contentDescription = "Imagem de ${relatedReceita.nome}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp) // Altura da imagem no card.
                                    )
                                    Text(
                                        relatedReceita.nome,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(8.dp),
                                        maxLines = 2, // Limita o nome a duas linhas.
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Mensagem se não houver receitas relacionadas.
                    Text("Nenhuma receita relacionada encontrada.", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espaço extra no final da rolagem.
            }
        } ?: run { // Executado se `receita` for nulo (receita não encontrada).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Centraliza a mensagem de erro.
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ops!", style = MaterialTheme.typography.headlineMedium)
                    Text("Receita não encontrada.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar para Início")
                    }
                }
            }
        }
    }
}

/**
 * Função para obter receitas relacionadas com base em palavras-chave comuns
 * nos ingredientes e no nome da receita.
 *
 * @param currentRecipe A receita atual para a qual encontrar relacionadas.
 * @param allRecipes A lista de todas as receitas disponíveis.
 * @return Uma lista de até 3 receitas relacionadas, ordenadas pela contagem de palavras-chave em comum.
 */
fun getRelatedRecipes(currentRecipe: Receita, allRecipes: List<Receita>): List<Receita> {
    // Filtra a receita atual da lista de todas as receitas.
    val otherRecipes = allRecipes.filter { it.id != currentRecipe.id }

    // Extrai palavras-chave da receita atual (ingredientes e nome).
    // Converte para minúsculas, divide por espaços/vírgulas, filtra palavras curtas/vazias.
    val currentKeywords = (currentRecipe.ingredientes.map { it.lowercase(Locale.getDefault()) } +
            currentRecipe.nome.lowercase(Locale.getDefault()).split(" ", "-")) // Adiciona hífen como separador
        .flatMap { it.split(" ", ",", ";", ".") } // Divide por mais separadores comuns.
        .map { it.filter { char -> char.isLetterOrDigit() } } // Remove caracteres não alfanuméricos.
        .filter { it.isNotBlank() && it.length > 2 } // Remove palavras vazias ou muito curtas.
        .toSet() // Converte para Set para remoção de duplicatas e busca eficiente.

    // Se não houver palavras-chave válidas na receita atual, retorna 3 receitas aleatórias.
    if (currentKeywords.isEmpty()) {
        return otherRecipes.shuffled().take(3) // Pega 3 aleatórias.
    }

    val relatedRecipesWithScore = mutableListOf<Pair<Receita, Int>>()

    // Itera sobre as outras receitas para calcular a pontuação de similaridade.
    for (otherRecipe in otherRecipes) {
        // Extrai palavras-chave da outra receita.
        val otherKeywords = (otherRecipe.ingredientes.map { it.lowercase(Locale.getDefault()) } +
                otherRecipe.nome.lowercase(Locale.getDefault()).split(" ", "-"))
            .flatMap { it.split(" ", ",", ";", ".") }
            .map { it.filter { char -> char.isLetterOrDigit() } }
            .filter { it.isNotBlank() && it.length > 2 }
            .toSet()

        // Conta o número de palavras-chave em comum.
        val commonWordsCount = currentKeywords.intersect(otherKeywords).size

        if (commonWordsCount > 0) { // Se houver alguma palavra em comum.
            relatedRecipesWithScore.add(Pair(otherRecipe, commonWordsCount))
        }
    }

    // Ordena as receitas pela contagem de palavras em comum (descendente) e pega as 3 primeiras.
    return relatedRecipesWithScore.sortedByDescending { it.second }
        .map { it.first }
        .take(3)
}
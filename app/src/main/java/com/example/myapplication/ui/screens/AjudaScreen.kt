package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable // Importado para uso futuro, se necessário
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar com suporte RTL
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline // Ícone para o cabeçalho da FAQ
import androidx.compose.material.icons.filled.MoreVert // Ícone de "mais opções"
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.model.DadosMockados // Dados mockados para FAQ
import com.example.myapplication.model.FAQItem // Modelo de dados para FAQ
import com.example.myapplication.ui.components.BottomNavigationBar

/**
 * Composable que exibe a tela de Ajuda e Perguntas Frequentes (FAQ).
 * Apresenta uma lista de perguntas e respostas que podem ser expandidas/recolhidas.
 *
 * @param navController Controlador de navegação para permitir voltar à tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necessário para componentes Material 3
@Composable
fun AjudaScreen(navController: NavHostController) {
    // Obtém a lista de FAQs dos dados mockados.
    val faqs = DadosMockados.listaDePerguntasFrequentes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajuda e FAQ") },
                navigationIcon = {
                    // Botão para voltar à tela anterior.
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Botão de "mais opções" (menu). Atualmente sem ação definida.
                    IconButton(onClick = { /* TODO: Implementar menu de opções se necessário */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) } // Barra de navegação inferior.
    ) { paddingValues -> // paddingValues do Scaffold para evitar sobreposição.
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues) // Aplica o padding do Scaffold.
                .fillMaxSize() // Ocupa todo o espaço disponível.
                .padding(16.dp) // Padding interno para o conteúdo.
        ) {
            // Cabeçalho da seção de FAQ.
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.HelpOutline, // Ícone de interrogação.
                        contentDescription = "Ícone de Ajuda",
                        modifier = Modifier.size(MaterialTheme.typography.headlineSmall.fontSize.value.dp + 4.dp) // Tamanho do ícone baseado no texto
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espaçador.
                    Text(
                        text = "Perguntas Frequentes",
                        style = MaterialTheme.typography.headlineSmall, // Estilo de título.
                        fontWeight = FontWeight.Bold // Texto em negrito.
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espaçador abaixo do cabeçalho.
            }
            // Itera sobre a lista de FAQs e cria um card para cada item.
            items(faqs) { faqItem ->
                FAQItemCard(faqItem = faqItem) // Composable para exibir um único item de FAQ.
                Spacer(modifier = Modifier.height(8.dp)) // Espaçador entre os cards.
            }

            // Seção de contato (simulada)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Ainda precisa de ajuda?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Implementar ação de contato real */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fale com o Suporte (Simulado)")
                }
            }
        }
    }
}

/**
 * Composable que exibe um único item de FAQ em um Card.
 * A resposta pode ser expandida ou recolhida ao clicar no card.
 *
 * @param faqItem O objeto [FAQItem] contendo a pergunta e a resposta.
 */
@Composable
fun FAQItemCard(faqItem: FAQItem) {
    // Estado para controlar se a resposta está expandida ou recolhida.
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth() // O card ocupa toda a largura.
            .clickable { expanded = !expanded }, // Alterna o estado 'expanded' ao clicar.
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Elevação sutil do card.
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Cor de fundo do card.
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Padding interno do card.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Pergunta à esquerda, ícone à direita.
                verticalAlignment = Alignment.CenterVertically // Alinha verticalmente ao centro.
            ) {
                // Texto da pergunta.
                Text(
                    text = faqItem.pergunta,
                    style = MaterialTheme.typography.titleSmall, // Estilo para a pergunta.
                    fontWeight = FontWeight.SemiBold, // Peso da fonte.
                    modifier = Modifier.weight(1f) // Ocupa o espaço restante, empurrando o ícone.
                )
                // Ícone para indicar se está expandido ou recolhido.
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                )
            }
            // Se estiver expandido, exibe a resposta.
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp)) // Espaçador entre a pergunta e a resposta.
                Text(
                    text = faqItem.resposta,
                    style = MaterialTheme.typography.bodyMedium // Estilo para a resposta.
                )
            }
        }
    }
}

// A função Divider não é mais necessária aqui, pois HorizontalDivider é usado diretamente
// ou o espaçamento é feito com Spacer. Se for um componente reutilizável específico,
// ele deve ser movido para ui/components e ter seu próprio KDoc.
// @Composable
// fun Divider() {
//    androidx.compose.material3.HorizontalDivider() // Usar HorizontalDivider para clareza
// }
package com.example.myapplication.ui.components

// import androidx.compose.foundation.background // Não utilizado
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding // Para adicionar padding considerando as barras de navegação do sistema
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn // Para definir largura mínima/máxima
import androidx.compose.foundation.lazy.LazyRow // Para uma lista horizontal rolável de botões
import androidx.compose.foundation.lazy.items // Para popular a LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector // Para os ícones dos botões
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController // Para controlar a navegação
import com.example.myapplication.navigation.AppScreens // Definições das rotas do app
// import com.example.myapplication.ui.theme.GreenTheme // Não utilizado diretamente aqui

/**
 * Representa um item na barra de navegação inferior.
 *
 * @property icon O [ImageVector] para o ícone do item.
 * @property label O texto do rótulo para o item.
 * @property route A rota de navegação para a qual este item direciona.
 */
data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

/**
 * Composable que exibe uma barra de navegação inferior personalizada usando uma [LazyRow].
 * Permite a navegação entre as principais telas do aplicativo.
 *
 * @param navController O [NavHostController] usado para executar as ações de navegação.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Lista dos itens de navegação a serem exibidos.
    val navItems = listOf(
        BottomNavItem(Icons.Default.Home, "Receitas", AppScreens.TelaInicialScreen.route),
        BottomNavItem(Icons.Default.Search, "Buscar", AppScreens.BuscaScreen.route),
        BottomNavItem(Icons.Default.Favorite, "Favoritos", AppScreens.FavoritosScreen.route),
        BottomNavItem(Icons.Default.Settings, "Configurações", AppScreens.ConfiguracoesScreen.route),
        BottomNavItem(Icons.Default.ShoppingCart, "Lista", AppScreens.ListaComprasScreen.route) // Label ajustado para "Lista" para caber melhor
    )

    // LazyRow para exibir os botões horizontalmente, permitindo rolagem se houver muitos itens.
    LazyRow(
        modifier = Modifier
            .fillMaxWidth() // Ocupa toda a largura disponível.
            .padding(horizontal = 8.dp, vertical = 4.dp) // Padding nas bordas da LazyRow.
            .navigationBarsPadding(), // Adiciona padding para não sobrepor as barras de sistema (ex: barra de gestos).
        horizontalArrangement = Arrangement.SpaceAround, // Distribui os itens com espaço igual ao redor deles.
        verticalAlignment = Alignment.CenterVertically // Alinha os itens verticalmente ao centro.
    ) {
        items(navItems) { item -> // Itera sobre a lista de navItems.
            // Cria um botão para cada item de navegação.
            BottomNavButton(
                icon = item.icon,
                label = item.label,
                onClick = {
                    // Navega para a rota do item quando clicado.
                    // Inclui lógica para evitar múltiplas cópias da mesma tela no backstack (popUpTo, launchSingleTop).
                    navController.navigate(item.route) {
                        // Remove todas as telas do backstack até a tela inicial antes de navegar.
                        // Isso evita um backstack grande se o usuário navegar entre as mesmas telas repetidamente.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true // Salva o estado da tela inicial.
                        }
                        launchSingleTop = true // Evita empilhar múltiplas instâncias da mesma tela.
                        restoreState = true // Restaura o estado se estiver navegando de volta para uma tela existente.
                    }
                }
            )
            // Adiciona um espaçador entre os botões se não for o último item (opcional, dependendo do Arrangement).
            // if (item != navItems.last()) {
            // Spacer(Modifier.width(8.dp))
            // }
        }
    }
}

/**
 * Composable privado que representa um botão individual na barra de navegação inferior.
 *
 * @param icon O [ImageVector] do ícone do botão.
 * @param label O texto do rótulo do botão.
 * @param onClick A ação a ser executada quando o botão é clicado.
 */
@Composable
private fun BottomNavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            // Define as cores do botão usando o esquema de cores do tema.
            containerColor = MaterialTheme.colorScheme.primaryContainer, // Cor de fundo do botão.
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer // Cor do conteúdo (ícone e texto).
        ),
        modifier = Modifier
            .height(48.dp) // Altura aumentada para melhor toque.
            .widthIn(min = 80.dp) // Largura mínima para acomodar ícone e texto.
            .padding(horizontal = 4.dp) // Padding horizontal dentro do botão.
    ) {
        Icon(icon, contentDescription = label) // Ícone do botão.
        Spacer(Modifier.width(4.dp)) // Pequeno espaçador entre o ícone e o texto.
        Text(label, style = MaterialTheme.typography.labelMedium) // Rótulo do botão, estilo de texto ajustado.
    }
}
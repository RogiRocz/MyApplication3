// app/src/main/java/com/example/myapplication/navigation/AppNavigation.kt

package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.ui.screens.AjudaScreen
import com.example.myapplication.ui.screens.BuscaScreen
import com.example.myapplication.ui.screens.ConfiguracoesScreen
import com.example.myapplication.ui.screens.DetalheScreen
import com.example.myapplication.ui.screens.FavoritosScreen
import com.example.myapplication.ui.screens.FeedbackScreen
import com.example.myapplication.ui.screens.ListaComprasScreen
import com.example.myapplication.ui.screens.TelaInicial
import com.example.myapplication.viewmodel.ListaComprasViewModel
import com.example.myapplication.viewmodel.BuscaViewModel

/**
 * Sealed class que define todas as telas (rotas) disponíveis no aplicativo.
 * Cada objeto representa uma tela específica e contém sua rota como uma string.
 * Isso ajuda a gerenciar a navegação de forma type-safe.
 *
 * @property route A string da rota usada pelo NavController para identificar a tela.
 */
sealed class AppScreens(val route: String) {
    /** Tela inicial do aplicativo. */
    object TelaInicialScreen : AppScreens("tela_inicial")
    /** Tela de receitas favoritas. */
    object FavoritosScreen : AppScreens("favoritos")
    /** Tela de configurações do aplicativo. */
    object ConfiguracoesScreen : AppScreens("configuracoes")
    /** Tela de ajuda e FAQ. */
    object AjudaScreen : AppScreens("ajuda")
    /** Tela de busca de receitas. */
    object BuscaScreen : AppScreens("busca")
    /** Tela da lista de compras. */
    object ListaComprasScreen: AppScreens("lista_compras")
    /** Tela para envio de feedback. */
    object FeedbackScreen : AppScreens("feedback_screen")

    /**
     * Tela de detalhes de uma receita específica.
     * A rota inclui um placeholder `{receitaId}` para o ID da receita.
     */
    object DetalheScreen : AppScreens("detalhe_receita/{receitaId}") {
        /**
         * Cria a rota completa para a tela de detalhes, substituindo o placeholder pelo ID da receita.
         * @param receitaId O ID da receita a ser exibida.
         * @return A string da rota formatada, por exemplo, "detalhe_receita/123".
         */
        fun createRoute(receitaId: Int): String {
            return "detalhe_receita/$receitaId"
        }
    }
}

/**
 * Composable principal que configura o grafo de navegação do aplicativo usando o NavHost.
 * Gerencia a navegação entre as diferentes telas definidas em [AppScreens].
 *
 * @param settingsViewModel ViewModel para acessar e modificar as configurações do aplicativo.
 *                          É passado para as telas que precisam dessas informações (ex: ConfiguracoesScreen).
 */
@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {
    // Cria e lembra uma instância do NavController.
    // O NavController é responsável por gerenciar o backstack e as transições entre as telas.
    val navController = rememberNavController()

    // Obtém instâncias dos ViewModels necessários para algumas telas.
    // A função viewModel() é uma forma padrão de obter ViewModels em Composables.
    val listaComprasViewModel: ListaComprasViewModel = viewModel()
    val buscaViewModel: BuscaViewModel = viewModel()

    // NavHost é o contêiner que hospeda os diferentes destinos (telas) do grafo de navegação.
    NavHost(
        navController = navController, // O NavController que gerenciará este NavHost.
        startDestination = AppScreens.TelaInicialScreen.route // A tela inicial a ser exibida.
    ) {
        // Define cada tela (composable) e sua rota correspondente.
        composable(AppScreens.TelaInicialScreen.route) {
            TelaInicial(navController)
        }
        composable(AppScreens.FavoritosScreen.route) {
            FavoritosScreen(navController)
        }
        composable(AppScreens.ConfiguracoesScreen.route) {
            ConfiguracoesScreen(navController, settingsViewModel)
        }
        composable(AppScreens.AjudaScreen.route) {
            AjudaScreen(navController)
        }
        composable(AppScreens.BuscaScreen.route) {
            BuscaScreen(navController, buscaViewModel)
        }
        // Para a tela de DetalheScreen, extrai o argumento 'receitaId' da rota.
        composable(AppScreens.DetalheScreen.route) { backStackEntry ->
            // Obtém o argumento 'receitaId' da rota.
            val receitaId = backStackEntry.arguments?.getString("receitaId")?.toIntOrNull()
            if (receitaId != null) {
                // Se o ID for válido, navega para DetalheScreen.
                DetalheScreen(navController = navController, receitaId = receitaId, listaComprasViewModel = listaComprasViewModel)
            } else {
                // Se o ID for inválido ou ausente, exibe uma mensagem de erro.
                // Idealmente, deveria haver um tratamento de erro mais robusto aqui (ex: navegar para uma tela de erro).
                Text("Erro: receitaId inválido ou ausente")
            }
        }
        composable(AppScreens.ListaComprasScreen.route) {
            ListaComprasScreen(navController = navController, listaComprasViewModel = listaComprasViewModel)
        }
        composable(AppScreens.FeedbackScreen.route) {
            FeedbackScreen(navController = navController)
        }
    }
}
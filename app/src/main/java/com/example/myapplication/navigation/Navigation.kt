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

sealed class AppScreens(val route: String) {
    object TelaInicialScreen : AppScreens("tela_inicial")
    object FavoritosScreen : AppScreens("favoritos")
    object ConfiguracoesScreen : AppScreens("configuracoes")
    object AjudaScreen : AppScreens("ajuda")
    object BuscaScreen : AppScreens("busca")
    object ListaComprasScreen: AppScreens("lista_compras")
    object FeedbackScreen : AppScreens("feedback_screen")

    object DetalheScreen : AppScreens("detalhe_receita/{receitaId}") {
        fun createRoute(receitaId: Int): String {
            return "detalhe_receita/$receitaId"
        }
    }
}

@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val listaComprasViewModel: ListaComprasViewModel = viewModel()
    val buscaViewModel: BuscaViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.TelaInicialScreen.route
    ) {
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
        composable(AppScreens.DetalheScreen.route) { backStackEntry ->
            val receitaId = backStackEntry.arguments?.getString("receitaId")?.toIntOrNull()
            if (receitaId != null) {
                DetalheScreen(navController = navController, receitaId = receitaId, listaComprasViewModel = listaComprasViewModel)
            } else {
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
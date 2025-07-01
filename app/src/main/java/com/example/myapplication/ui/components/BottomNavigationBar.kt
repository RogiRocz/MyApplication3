package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.theme.GreenTheme


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navItems = listOf(
        BottomNavItem(Icons.Default.Home, "Receitas", AppScreens.TelaInicialScreen.route),
        BottomNavItem(Icons.Default.Search, "Buscar", AppScreens.BuscaScreen.route),
        BottomNavItem(Icons.Default.Favorite, "Favoritos", AppScreens.FavoritosScreen.route),
        BottomNavItem(Icons.Default.Settings, "Configurações", AppScreens.ConfiguracoesScreen.route),
        BottomNavItem(Icons.Default.ShoppingCart, "Lista de Compras", AppScreens.ListaComprasScreen.route)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(navItems) { item ->
            BottomNavButton(
                icon = item.icon,
                label = item.label,
                onClick = { navController.navigate(item.route) }
            )
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
private fun BottomNavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .height(40.dp)
            .widthIn(min = 64.dp)
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)
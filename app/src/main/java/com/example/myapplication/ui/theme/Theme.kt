package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme // Para detectar o tema do sistema.
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme // Esquema de cores padrão para tema escuro.
import androidx.compose.material3.dynamicDarkColorScheme // Esquema de cores dinâmico para tema escuro (Android 12+).
import androidx.compose.material3.dynamicLightColorScheme // Esquema de cores dinâmico para tema claro (Android 12+).
import androidx.compose.material3.lightColorScheme // Esquema de cores padrão para tema claro.
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect // Para executar efeitos colaterais após a composição.
import androidx.compose.ui.graphics.toArgb // Para converter Color do Compose para um valor ARGB Int.
import androidx.compose.ui.platform.LocalContext // Para obter o contexto local.
import androidx.compose.ui.platform.LocalView // Para obter a View atual.
import androidx.core.view.WindowCompat // Para controlar a aparência da janela (barra de status).

// Esquema de cores personalizado para o tema escuro.
// Estas cores (Purple80, PurpleGrey80, Pink80) são definidas em Color.kt.
private val DarkColorScheme = darkColorScheme(
    primary = Purple80, // Cor primária principal.
    secondary = PurpleGrey80, // Cor secundária.
    tertiary = Pink80 // Cor terciária.
    // Outras cores como background, surface, onPrimary, etc., usarão os padrões do darkColorScheme
    // se não forem explicitamente sobrescritas aqui.
)

// Esquema de cores personalizado para o tema claro.
// Estas cores (Purple40, PurpleGrey40, Pink40) são definidas em Color.kt.
private val LightColorScheme = lightColorScheme(
    primary = Purple40, // Cor primária principal.
    secondary = PurpleGrey40, // Cor secundária.
    tertiary = Pink40 // Cor terciária.

    /* Você pode sobrescrever outras cores padrão aqui, se necessário:
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F), // Cor do texto/ícones sobre o background.
    onSurface = Color(0xFF1C1B1F),  // Cor do texto/ícones sobre superfícies.
    */
)

/**
 * Composable que aplica o tema do aplicativo NutriLivre ao seu conteúdo.
 * Suporta temas claro e escuro, e cores dinâmicas (Material You) no Android 12+.
 * Também ajusta a cor da barra de status para combinar com o tema.
 *
 * @param darkTheme Boolean que indica se o tema escuro deve ser aplicado.
 *                  Por padrão, usa a configuração do sistema (`isSystemInDarkTheme()`).
 * @param dynamicColor Boolean que indica se as cores dinâmicas (Material You) devem ser usadas
 *                     em dispositivos compatíveis (Android 12+). Padrão é `true`.
 * @param content O conteúdo Composable ao qual o tema será aplicado.
 */
@Composable
fun NutriLivreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Usa o tema do sistema por padrão.
    dynamicColor: Boolean = true, // Habilita cores dinâmicas por padrão em S+
    content: @Composable () -> Unit // O conteúdo da UI que usará este tema.
) {
    // Determina o esquema de cores a ser usado.
    val colorScheme = when {
        // Se cores dinâmicas estiverem habilitadas e o dispositivo for Android S (API 31) ou superior:
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Usa dynamicDarkColorScheme ou dynamicLightColorScheme com base no parâmetro darkTheme.
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Se cores dinâmicas não estiverem disponíveis ou desabilitadas, usa os esquemas personalizados.
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Obtém a View atual. Necessário para modificar a barra de status.
    val view = LocalView.current
    // `!view.isInEditMode` garante que o código de modificação da barra de status
    // não seja executado durante a pré-visualização no Android Studio, o que causaria um crash.
    if (!view.isInEditMode) {
        // `SideEffect` é usado para executar código que modifica o ambiente fora do Compose
        // de forma segura após a composição.
        SideEffect {
            val window = (view.context as Activity).window // Obtém a janela da Activity.
            // Define a cor da barra de status para a cor primária do tema.
            window.statusBarColor = colorScheme.primary.toArgb()
            // Define a aparência dos ícones da barra de status (claros ou escuros)
            // com base no tema (escuro ou claro).
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // Ícones claros para tema escuro, escuros para tema claro.
        }
    }

    // Aplica o MaterialTheme com o colorScheme selecionado, a tipografia definida (Typography.kt)
    // e o conteúdo fornecido.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Definido em Type.kt
        content = content
    )
}
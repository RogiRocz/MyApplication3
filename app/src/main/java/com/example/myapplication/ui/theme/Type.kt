package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily // Para definir famílias de fontes (padrão, sans-serif, serif, etc.)
import androidx.compose.ui.text.font.FontWeight // Para definir o peso da fonte (Normal, Bold, etc.)
import androidx.compose.ui.unit.sp // Unidade de medida para tamanho de fonte (Scale-independent Pixels).

// Define o conjunto de estilos de tipografia do Material Design para o aplicativo.
// Estes estilos são usados por padrão pelos componentes do MaterialTheme.
// Você pode personalizar esses estilos ou adicionar novos conforme necessário.

/**
 * Objeto [Typography] que define os estilos de texto para o tema do aplicativo.
 *
 * Este objeto personaliza o estilo `bodyLarge` e fornece exemplos comentados
 * de como outros estilos padrão (`titleLarge`, `labelSmall`) podem ser sobrescritos.
 *
 * Para usar fontes personalizadas, você precisaria:
 * 1. Adicionar os arquivos de fonte (ex: .ttf ou .otf) à pasta `res/font`.
 * 2. Criar um `FontFamily` referenciando essas fontes. Exemplo:
 *    ```kotlin
 *    val MontserratFontFamily = FontFamily(
 *        Font(R.font.montserrat_regular, FontWeight.Normal),
 *        Font(R.font.montserrat_bold, FontWeight.Bold),
 *        Font(R.font.montserrat_semibold, FontWeight.SemiBold)
 *    )
 *    ```
 * 3. Atribuir essa `FontFamily` personalizada aos `TextStyle`s desejados.
 */
val Typography = Typography(
    // Estilo padrão para o corpo de texto principal.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Usa a família de fontes padrão do sistema.
        fontWeight = FontWeight.Normal, // Peso da fonte normal.
        fontSize = 16.sp, // Tamanho da fonte.
        lineHeight = 24.sp, // Altura da linha para melhor legibilidade.
        letterSpacing = 0.5.sp // Espaçamento entre letras.
    )

    // Exemplos de como sobrescrever outros estilos de texto padrão.
    // Descomente e personalize conforme necessário.
    /*
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default, // Ou sua FontFamily personalizada
        fontWeight = FontWeight.Bold,    // Títulos grandes geralmente são em negrito
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // Rótulos pequenos podem ter um peso médio.
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Você pode definir todos os estilos de tipografia do Material 3 aqui:
    // displayLarge, displayMedium, displaySmall
    // headlineLarge, headlineMedium, headlineSmall
    // titleLarge, titleMedium, titleSmall
    // bodyLarge, bodyMedium, bodySmall
    // labelLarge, labelMedium, labelSmall
    */
)
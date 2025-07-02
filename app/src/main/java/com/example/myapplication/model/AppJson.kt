package com.example.myapplication.utils

import kotlinx.serialization.json.Json

/**
 * Instância singleton de [Json] configurada para uso em todo o aplicativo.
 * Esta instância é configurada com as seguintes propriedades:
 * - `prettyPrint = true`: Formata a saída JSON de forma legível, com indentação e quebras de linha.
 * Útil para debugging e logs.
 * - `ignoreUnknownKeys = true`: Permite que o parser JSON ignore chaves desconhecidas no JSON de entrada
 * ao desserializar objetos. Isso torna o parsing mais robusto a mudanças na API ou dados JSON.
 * - `isLenient = true`: Permite que o parser JSON seja mais tolerante com JSON malformado (por exemplo,
 * strings não citadas como chaves). Embora útil em alguns cenários, deve ser usado com cautela.
 */
val AppJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}
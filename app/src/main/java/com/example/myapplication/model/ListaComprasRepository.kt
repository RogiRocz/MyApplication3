package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.ListaComprasItem
import com.example.myapplication.utils.AppJson // Importa a instância configurada do Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString // Import necessário para decodeFromString

/**
 * Extensão para o Context que fornece acesso ao DataStore de preferências específico para a lista de compras.
 * O nome "lista_compras_prefs" é usado para identificar este DataStore.
 */
val Context.listaComprasDataStore: DataStore<Preferences> by preferencesDataStore(name = "lista_compras_prefs")

/**
 * Repositório para gerenciar os dados da lista de compras.
 * Utiliza o DataStore para persistir a lista de compras como uma string JSON.
 *
 * @param context O contexto do aplicativo, usado para acessar o DataStore.
 */
class ListaComprasRepository(private val context: Context) {

    // Chave para armazenar a string JSON da lista de compras no DataStore.
    private val LISTA_COMPRAS_KEY = stringPreferencesKey("lista_compras_key")

    /**
     * Um Flow que emite a lista atual de [ListaComprasItem].
     * Lê a string JSON do DataStore, a desserializa para uma lista de objetos `ListaComprasItem`.
     * Em caso de erro na desserialização (por exemplo, JSON malformado ou dados corrompidos),
     * imprime o stack trace do erro e retorna uma lista vazia para evitar que o app quebre.
     *
     * @return Um Flow que emite a lista de itens de compra.
     */
    val getListaCompras: Flow<List<ListaComprasItem>> = context.listaComprasDataStore.data
        .map { preferences ->
            val jsonString = preferences[LISTA_COMPRAS_KEY] ?: "[]" // Padrão para uma string JSON de lista vazia
            try {
                // Desserializa a string JSON para uma lista de ListaComprasItem
                AppJson.decodeFromString<List<ListaComprasItem>>(jsonString)
            } catch (e: Exception) {
                // Tratamento de erro: loga a exceção e retorna uma lista vazia
                e.printStackTrace() // Idealmente, usar um logger mais robusto aqui
                emptyList<ListaComprasItem>() // Retorna lista vazia em caso de falha
            }
        }

    /**
     * Salva a lista de compras fornecida no DataStore.
     * A lista é serializada para uma string JSON antes de ser salva.
     *
     * @param lista A lista de [ListaComprasItem] a ser salva.
     */
    suspend fun saveListaCompras(lista: List<ListaComprasItem>) {
        context.listaComprasDataStore.edit { preferences ->
            // Serializa a lista para uma string JSON
            val jsonString = AppJson.encodeToString(lista)
            preferences[LISTA_COMPRAS_KEY] = jsonString
        }
    }
}
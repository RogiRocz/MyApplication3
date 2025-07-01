package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.model.ListaComprasItem
import com.example.myapplication.utils.AppJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

val Context.listaComprasDataStore: DataStore<Preferences> by preferencesDataStore(name = "lista_compras_prefs")

class ListaComprasRepository(private val context: Context) {

    private val LISTA_COMPRAS_KEY = stringPreferencesKey("lista_compras_key")

    val getListaCompras: Flow<List<ListaComprasItem>> = context.listaComprasDataStore.data
        .map { preferences ->
            val jsonString = preferences[LISTA_COMPRAS_KEY] ?: "[]"
            try {
                AppJson.decodeFromString<List<ListaComprasItem>>(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun saveListaCompras(lista: List<ListaComprasItem>) {
        context.listaComprasDataStore.edit { preferences ->
            val jsonString = AppJson.encodeToString(lista)
            preferences[LISTA_COMPRAS_KEY] = jsonString
        }
    }
}
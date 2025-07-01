package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.model.Receita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BuscaViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Receita>>(emptyList())
    val searchResults: StateFlow<List<Receita>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        if (text.isNotBlank()) {
            performSearch(text)
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val filteredRecipes = DadosMockados.listaDeReceitas.filter { receita ->
                receita.nome.contains(query, ignoreCase = true) ||
                        receita.ingredientes.any { it.contains(query, ignoreCase = true) }
            }
            _searchResults.value = filteredRecipes
            _isLoading.value = false
        }
    }

    fun searchManually() {
        if (_searchText.value.isNotBlank()) {
            performSearch(_searchText.value)
        }
    }
}
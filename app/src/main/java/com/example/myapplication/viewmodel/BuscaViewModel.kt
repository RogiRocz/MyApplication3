package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.DadosMockados // Fonte dos dados mockados de receitas
import com.example.myapplication.model.Receita // Modelo da classe Receita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay // Para simular um atraso na busca
import kotlinx.coroutines.launch // Para executar operações assíncronas

/**
 * ViewModel para a tela de Busca ([BuscaScreen]).
 * Gerencia o estado da busca, incluindo o texto digitado pelo usuário,
 * os resultados da busca e o estado de carregamento.
 */
class BuscaViewModel : ViewModel() {

    // StateFlow privado para o texto da busca, mutável apenas dentro do ViewModel.
    private val _searchText = MutableStateFlow("")
    /**
     * [StateFlow] público e imutável que expõe o texto atual da busca.
     * A UI observa este Flow para reagir a mudanças no texto de busca.
     */
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // StateFlow privado para os resultados da busca.
    private val _searchResults = MutableStateFlow<List<Receita>>(emptyList())
    /**
     * [StateFlow] público e imutável que expõe a lista de receitas resultantes da busca.
     * A UI observa este Flow para exibir os resultados.
     */
    val searchResults: StateFlow<List<Receita>> = _searchResults.asStateFlow()

    // StateFlow privado para o estado de carregamento da busca.
    private val _isLoading = MutableStateFlow(false)
    /**
     * [StateFlow] público e imutável que indica se uma busca está em progresso.
     * A UI pode usar isso para exibir um indicador de carregamento.
     */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Chamado quando o texto no campo de busca é alterado pelo usuário.
     * Atualiza o [_searchText] e inicia uma nova busca se o texto não estiver em branco.
     * Se o texto estiver em branco, limpa os resultados da busca.
     *
     * @param text O novo texto digitado no campo de busca.
     */
    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        if (text.isNotBlank()) {
            // Inicia a busca se o texto não for vazio.
            performSearch(text)
        } else {
            // Limpa os resultados se o texto estiver vazio.
            _searchResults.value = emptyList()
        }
    }

    /**
     * Executa a lógica de busca de forma assíncrona.
     * Filtra a lista de receitas mockadas ([DadosMockados.listaDeReceitas])
     * com base no nome da receita ou nos ingredientes.
     * Simula um atraso de rede/processamento com `delay(1000)`.
     *
     * @param query A string de busca a ser usada para filtrar as receitas.
     */
    private fun performSearch(query: String) {
        viewModelScope.launch { // Lança uma coroutine no escopo do ViewModel.
            _isLoading.value = true // Define o estado de carregamento como verdadeiro.
            delay(1000) // Simula um atraso (ex: chamada de rede). Remover em produção real.

            // Filtra a lista de receitas.
            // A busca não diferencia maiúsculas de minúsculas (ignoreCase = true).
            val filteredRecipes = DadosMockados.listaDeReceitas.filter { receita ->
                receita.nome.contains(query, ignoreCase = true) || // Verifica o nome da receita
                        receita.ingredientes.any { it.contains(query, ignoreCase = true) } // Verifica os ingredientes
            }
            _searchResults.value = filteredRecipes // Atualiza os resultados da busca.
            _isLoading.value = false // Define o estado de carregamento como falso.
        }
    }

    /**
     * Função para iniciar manualmente uma busca com o texto atual.
     * Pode ser útil para um botão de "buscar" explícito, embora o comportamento atual
     * seja de busca automática à medida que o usuário digita.
     */
    fun searchManually() {
        if (_searchText.value.isNotBlank()) {
            performSearch(_searchText.value)
        }
    }
}
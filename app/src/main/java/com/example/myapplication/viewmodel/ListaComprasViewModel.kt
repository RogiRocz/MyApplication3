package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ListaComprasRepository // Repositório para persistência de dados
import com.example.myapplication.model.ListaComprasItem // Modelo do item da lista de compras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Para atualizar MutableStateFlow de forma concisa
import kotlinx.coroutines.flow.map // Para transformar Flows
import kotlinx.coroutines.flow.SharingStarted // Para configurar como o StateFlow é compartilhado
import kotlinx.coroutines.flow.stateIn // Para converter um Flow em StateFlow
import kotlinx.coroutines.launch // Para executar operações assíncronas

/**
 * ViewModel para a tela da Lista de Compras ([ListaComprasScreen]).
 * Herda de [AndroidViewModel] para ter acesso ao contexto do aplicativo,
 * necessário para inicializar o [ListaComprasRepository].
 *
 * Gerencia o estado da lista de compras, incluindo os itens, itens selecionados (para modo de seleção múltipla),
 * o progresso de itens comprados e o modo de seleção múltipla.
 *
 * @param application A instância da aplicação, usada para obter o contexto.
 */
class ListaComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ListaComprasRepository // Instância do repositório
    // StateFlow privado para a lista de compras, mutável apenas dentro do ViewModel.
    private val _listaCompras = MutableStateFlow<List<ListaComprasItem>>(emptyList())
    /** [StateFlow] público e imutável que expõe a lista de compras atual. */
    val listaCompras: StateFlow<List<ListaComprasItem>> = _listaCompras.asStateFlow()

    // StateFlow privado para o conjunto de IDs dos itens selecionados no modo de seleção múltipla.
    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    /** [StateFlow] público e imutável que expõe os itens atualmente selecionados. */
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    /**
     * [StateFlow] que calcula e emite o progresso (0.0 a 1.0) de itens comprados na lista.
     * `SharingStarted.WhileSubscribed()`: O Flow é ativado quando há observadores e desativado
     *                                      quando não há mais, economizando recursos.
     * `initialValue = 0f`: Valor inicial até que a lista seja carregada.
     */
    val progress: StateFlow<Float> = _listaCompras.map { list -> // Mapeia o Flow da lista de compras
        if (list.isEmpty()) {
            0f // Progresso é 0 se a lista estiver vazia.
        } else {
            // Calcula a proporção de itens comprados.
            list.count { it.isBought }.toFloat() / list.size.toFloat()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f) // Converte para StateFlow

    // StateFlow privado para indicar se o modo de seleção múltipla está ativo.
    private val _isMultiSelectionMode = MutableStateFlow(false)
    /** [StateFlow] público e imutável que indica se o modo de seleção múltipla está ativo. */
    val isMultiSelectionMode: StateFlow<Boolean> = _isMultiSelectionMode.asStateFlow()

    init {
        // Inicializa o repositório com o contexto da aplicação.
        repository = ListaComprasRepository(application.applicationContext)
        // Lança uma coroutine para coletar a lista de compras do repositório assim que o ViewModel é criado.
        viewModelScope.launch {
            repository.getListaCompras.collect { loadedList ->
                _listaCompras.value = loadedList // Atualiza o StateFlow com a lista carregada.
            }
        }
    }

    /**
     * Verifica se um item com o nome fornecido (ignorando maiúsculas/minúsculas e espaços extras)
     * já existe na lista de compras.
     *
     * @param itemName O nome do item a ser verificado.
     * @return `true` se o item já existe, `false` caso contrário.
     */
    private fun itemExists(itemName: String): Boolean {
        val trimmedName = itemName.trim() // Remove espaços em branco no início e fim.
        return _listaCompras.value.any { it.name.equals(trimmedName, ignoreCase = true) }
    }

    /**
     * Adiciona um novo item à lista de compras se o nome não estiver em branco e o item ainda não existir.
     *
     * @param itemName O nome do item a ser adicionado.
     */
    fun addItem(itemName: String) {
        val trimmedName = itemName.trim()
        if (trimmedName.isNotBlank() && !itemExists(trimmedName)) {
            _listaCompras.update { currentList ->
                val updatedList = currentList + ListaComprasItem(name = trimmedName) // Adiciona o novo item.
                saveList(updatedList) // Salva a lista atualizada no repositório.
                updatedList // Retorna a lista atualizada para o StateFlow.
            }
        }
    }

    /**
     * Adiciona múltiplos itens à lista de compras.
     * Itens com nomes em branco ou que já existem (ignorando caso e espaços) são ignorados.
     *
     * @param itemNames Lista de nomes dos itens a serem adicionados.
     */
    fun addItems(itemNames: List<String>) {
        _listaCompras.update { currentList ->
            val newItemsToAdd = itemNames
                .map { it.trim() } // Remove espaços
                .filter { it.isNotBlank() } // Filtra nomes não vazios
                .filter { newItemName -> // Filtra itens que ainda não existem na lista
                    !currentList.any { existingItem -> existingItem.name.equals(newItemName, ignoreCase = true) }
                }
                .map { ListaComprasItem(name = it) } // Mapeia para objetos ListaComprasItem
            val updatedList = currentList + newItemsToAdd
            saveList(updatedList)
            updatedList
        }
    }

    /**
     * Alterna o estado "comprado" de um item específico.
     *
     * @param itemId O ID do item cujo estado será alternado.
     */
    fun toggleBoughtStatus(itemId: String) {
        _listaCompras.update { currentList ->
            val updatedList = currentList.map { item ->
                if (item.id == itemId) item.copy(isBought = !item.isBought) else item
            }
            saveList(updatedList)
            updatedList
        }
    }

    /**
     * Remove um item da lista de compras.
     * Também remove o item da lista de seleção, se estiver presente.
     *
     * @param itemId O ID do item a ser removido.
     */
    fun deleteItem(itemId: String) {
        _listaCompras.update { currentList ->
            val updatedList = currentList.filter { it.id != itemId }
            saveList(updatedList)
            updatedList
        }
        // Remove o item da seleção, caso estivesse selecionado.
        _selectedItems.update { currentSelection ->
            currentSelection - itemId
        }
    }

    /**
     * Alterna a seleção de um item no modo de seleção múltipla.
     * Se o item já estiver selecionado, ele é desmarcado; caso contrário, é marcado.
     * Atualiza o estado `isMultiSelectionMode` com base se há itens selecionados.
     *
     * @param itemId O ID do item a ter sua seleção alternada.
     */
    fun toggleSelection(itemId: String) {
        _selectedItems.update { currentSelection ->
            if (currentSelection.contains(itemId)) {
                currentSelection - itemId // Remove da seleção
            } else {
                currentSelection + itemId // Adiciona à seleção
            }
        }
        // Atualiza o modo de seleção múltipla: ativo se houver algum item selecionado.
        _isMultiSelectionMode.update { _selectedItems.value.isNotEmpty() }
    }

    /**
     * Alterna o modo de seleção múltipla.
     * Se o modo for desativado, limpa a seleção atual.
     */
    fun toggleMultiSelectionMode() {
        _isMultiSelectionMode.update { !it } // Inverte o estado atual.
        if (!_isMultiSelectionMode.value) { // Se o modo foi desativado...
            clearSelection() // ...limpa a seleção.
        }
    }

    /**
     * Limpa todos os itens selecionados e desativa o modo de seleção múltipla.
     */
    fun clearSelection() {
        _selectedItems.update { emptySet() } // Limpa o conjunto de itens selecionados.
        _isMultiSelectionMode.update { false } // Desativa o modo de seleção múltipla.
    }

    /**
     * Remove todos os itens atualmente selecionados da lista de compras.
     * Após a remoção, limpa a seleção.
     */
    fun deleteSelectedItems() {
        _listaCompras.update { currentList ->
            val updatedList = currentList.filter { !_selectedItems.value.contains(it.id) }
            saveList(updatedList)
            updatedList
        }
        clearSelection() // Limpa a seleção após excluir os itens.
    }

    /**
     * Alterna o estado "comprado" de todos os itens na lista.
     * Se todos estiverem comprados, desmarca todos. Caso contrário, marca todos como comprados.
     */
    fun toggleAllItemsBoughtStatus() {
        _listaCompras.update { currentList ->
            if (currentList.isEmpty()) return@update currentList // Não faz nada se a lista estiver vazia.
            val allBought = currentList.all { it.isBought } // Verifica se todos os itens já estão comprados.
            val updatedList = currentList.map { it.copy(isBought = !allBought) } // Inverte o estado de todos.
            saveList(updatedList)
            updatedList
        }
    }

    /**
     * Remove todos os itens da lista de compras.
     * Também limpa a seleção, caso haja alguma.
     */
    fun clearShoppingList() {
        _listaCompras.update {
            val updatedList = emptyList<ListaComprasItem>()
            saveList(updatedList)
            updatedList
        }
        clearSelection() // Garante que a seleção seja limpa.
    }

    /**
     * Função privada para salvar a lista de compras atual no repositório.
     * Lançada em uma coroutine no `viewModelScope`.
     *
     * @param list A lista de [ListaComprasItem] a ser salva.
     */
    private fun saveList(list: List<ListaComprasItem>) {
        viewModelScope.launch {
            repository.saveListaCompras(list)
        }
    }
}
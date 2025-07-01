package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ListaComprasRepository
import com.example.myapplication.model.ListaComprasItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListaComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ListaComprasRepository
    private val _listaCompras = MutableStateFlow<List<ListaComprasItem>>(emptyList())
    val listaCompras: StateFlow<List<ListaComprasItem>> = _listaCompras.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    val progress: StateFlow<Float> = _listaCompras.asStateFlow().map { list ->
        if (list.isEmpty()) {
            0f
        } else {
            list.count { it.isBought }.toFloat() / list.size.toFloat()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    private val _isMultiSelectionMode = MutableStateFlow(false)
    val isMultiSelectionMode: StateFlow<Boolean> = _isMultiSelectionMode.asStateFlow()

    init {
        repository = ListaComprasRepository(application.applicationContext) // Inicialize o Repository
        viewModelScope.launch {
            repository.getListaCompras.collect { loadedList ->
                _listaCompras.value = loadedList
            }
        }
    }

    private fun itemExists(itemName: String): Boolean {
        val trimmedName = itemName.trim()
        return _listaCompras.value.any { it.name.equals(trimmedName, ignoreCase = true) }
    }

    fun addItem(itemName: String) {
        val trimmedName = itemName.trim()
        if (trimmedName.isNotBlank() && !itemExists(trimmedName)) {
            _listaCompras.update { currentList ->
                val updatedList = currentList + ListaComprasItem(name = trimmedName)
                saveList(updatedList)
                updatedList
            }
        }
    }

    fun addItems(itemNames: List<String>) {
        _listaCompras.update { currentList ->
            val newItemsToAdd = itemNames
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .filter { newItemName ->
                    !currentList.any { existingItem -> existingItem.name.equals(newItemName, ignoreCase = true) }
                }
                .map { ListaComprasItem(name = it) }
            val updatedList = currentList + newItemsToAdd
            saveList(updatedList)
            updatedList
        }
    }

    fun toggleBoughtStatus(itemId: String) {
        _listaCompras.update { currentList ->
            val updatedList = currentList.map { item ->
                if (item.id == itemId) item.copy(isBought = !item.isBought) else item
            }
            saveList(updatedList)
            updatedList
        }
    }

    fun deleteItem(itemId: String) {
        _listaCompras.update { currentList ->
            val updatedList = currentList.filter { it.id != itemId }
            saveList(updatedList)
            updatedList
        }
        _selectedItems.update { currentSelection ->
            currentSelection - itemId
        }
    }

    fun toggleSelection(itemId: String) {
        _selectedItems.update { currentSelection ->
            if (currentSelection.contains(itemId)) {
                currentSelection - itemId
            } else {
                currentSelection + itemId
            }
        }
        _isMultiSelectionMode.update { _selectedItems.value.isNotEmpty() }
    }

    fun toggleMultiSelectionMode() {
        _isMultiSelectionMode.update { !it }
        if (!_isMultiSelectionMode.value) {
            clearSelection()
        }
    }

    fun clearSelection() {
        _selectedItems.update { emptySet() }
        _isMultiSelectionMode.update { false }
    }

    fun deleteSelectedItems() {
        _listaCompras.update { currentList ->
            val updatedList = currentList.filter { !_selectedItems.value.contains(it.id) }
            saveList(updatedList)
            updatedList
        }
        clearSelection()
    }

    fun toggleAllItemsBoughtStatus() {
        _listaCompras.update { currentList ->
            val allBought = currentList.all { it.isBought }
            val updatedList = currentList.map { it.copy(isBought = !allBought) }
            saveList(updatedList)
            updatedList
        }
    }

    fun clearShoppingList() {
        _listaCompras.update {
            val updatedList = emptyList<ListaComprasItem>()
            saveList(updatedList)
            updatedList
        }
        clearSelection()
    }

    private fun saveList(list: List<ListaComprasItem>) {
        viewModelScope.launch {
            repository.saveListaCompras(list)
        }
    }
}
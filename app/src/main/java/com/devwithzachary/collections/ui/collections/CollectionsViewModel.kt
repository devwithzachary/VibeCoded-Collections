package com.devwithzachary.collections.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devwithzachary.collections.data.Collection
import com.devwithzachary.collections.data.CollectionsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CollectionsViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val collections: StateFlow<List<Collection>> =
        collectionsRepository.getAllCollections()
            .combine(_searchQuery) { collections, query ->
                if (query.isNotBlank()) {
                    collections.filter { it.name.contains(query, ignoreCase = true) }
                } else {
                    collections
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addCollection(name: String) {
        viewModelScope.launch {
            collectionsRepository.insertCollection(Collection(name = name))
        }
    }

    fun updateCollection(collection: Collection) {
        viewModelScope.launch {
            collectionsRepository.updateCollection(collection)
        }
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            collectionsRepository.deleteCollection(collection)
        }
    }
}

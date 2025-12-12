package com.devwithzachary.collections.ui.collectiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devwithzachary.collections.data.CollectionItem
import com.devwithzachary.collections.data.CollectionsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CollectionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val collectionId: Int = checkNotNull(savedStateHandle["collectionId"])

    private val _state = MutableStateFlow(CollectionDetailsState())
    val state: StateFlow<CollectionDetailsState> = _state.asStateFlow()

    val collectionItems: StateFlow<List<CollectionItem>> =
        collectionsRepository.getItemsForCollection(collectionId)
            .combine(_state) { items, state ->
                val filteredItems = when (state.filterType) {
                    FilterType.HAVE -> items.filter { it.has }
                    FilterType.WANT -> items.filter { it.wants }
                    FilterType.NONE -> items
                }
                val searchedItems = if (state.searchQuery.isNotBlank()) {
                    filteredItems.filter {
                        it.name.contains(state.searchQuery, ignoreCase = true) ||
                                it.description.contains(state.searchQuery, ignoreCase = true)
                    }
                } else {
                    filteredItems
                }
                when (state.sortType) {
                    SortType.ALPHABETICAL -> searchedItems.sortedBy { it.name }
                    SortType.ALPHABETICAL_REVERSE -> searchedItems.sortedByDescending { it.name }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onSortTypeChange(sortType: SortType) {
        _state.update { it.copy(sortType = sortType) }
    }

    fun onFilterTypeChange(filterType: FilterType) {
        _state.update { it.copy(filterType = filterType) }
    }

    fun addItem(name: String, description: String, has: Boolean, wants: Boolean, imageUri: String?) {
        viewModelScope.launch {
            collectionsRepository.insertCollectionItem(
                CollectionItem(
                    collectionId = collectionId,
                    name = name,
                    description = description,
                    has = has,
                    wants = wants,
                    imageUri = imageUri
                )
            )
        }
    }

    fun updateItem(item: CollectionItem) {
        viewModelScope.launch {
            collectionsRepository.updateCollectionItem(item)
        }
    }

    fun deleteItem(item: CollectionItem) {
        viewModelScope.launch {
            collectionsRepository.deleteCollectionItem(item)
        }
    }
}

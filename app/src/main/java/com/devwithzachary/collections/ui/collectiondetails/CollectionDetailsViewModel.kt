package com.devwithzachary.collections.ui.collectiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devwithzachary.collections.data.CollectionItem
import com.devwithzachary.collections.data.CollectionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CollectionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val collectionId: Int = checkNotNull(savedStateHandle["collectionId"])

    val collectionItems: StateFlow<List<CollectionItem>> =
        collectionsRepository.getItemsForCollection(collectionId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addItem(name: String, description: String, has: Boolean, wants: Boolean) {
        viewModelScope.launch {
            collectionsRepository.insertCollectionItem(
                CollectionItem(
                    collectionId = collectionId,
                    name = name,
                    description = description,
                    has = has,
                    wants = wants
                )
            )
        }
    }

    fun updateItem(item: CollectionItem) {
        viewModelScope.launch {
            collectionsRepository.updateCollectionItem(item)
        }
    }
}

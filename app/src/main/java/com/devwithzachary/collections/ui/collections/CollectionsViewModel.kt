package com.devwithzachary.collections.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.devwithzachary.collections.CollectionsApplication
import com.devwithzachary.collections.data.Collection
import com.devwithzachary.collections.data.CollectionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CollectionsViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    val collections: StateFlow<List<Collection>> =
        collectionsRepository.getAllCollections().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addCollection(name: String) {
        viewModelScope.launch {
            collectionsRepository.insertCollection(Collection(name = name))
        }
    }
}

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CollectionsViewModel(
                collectionsApplication().container.collectionsRepository
            )
        }
    }
}

fun CreationExtras.collectionsApplication(): CollectionsApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CollectionsApplication)

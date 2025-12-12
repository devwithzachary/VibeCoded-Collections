package com.devwithzachary.collections.ui.collections

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.devwithzachary.collections.CollectionsApplication
import com.devwithzachary.collections.ui.collectiondetails.CollectionDetailsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CollectionsViewModel(
                collectionsApplication().container.collectionsRepository,
                collectionsApplication()
            )
        }

        initializer {
            CollectionDetailsViewModel(
                this.createSavedStateHandle(),
                collectionsApplication().container.collectionsRepository
            )
        }
    }
}

fun CreationExtras.collectionsApplication(): CollectionsApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CollectionsApplication)

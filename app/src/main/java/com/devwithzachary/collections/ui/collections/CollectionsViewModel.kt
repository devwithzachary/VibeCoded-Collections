package com.devwithzachary.collections.ui.collections

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devwithzachary.collections.data.Collection
import com.devwithzachary.collections.data.CollectionsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class CollectionsViewModel(
    private val collectionsRepository: CollectionsRepository,
    private val application: Application
) : AndroidViewModel(application) {

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

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            try {
                val json = collectionsRepository.exportData()
                application.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            try {
                val stringBuilder = StringBuilder()
                application.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }
                collectionsRepository.importData(stringBuilder.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

package com.devwithzachary.collections.ui.collections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devwithzachary.collections.data.Collection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCollectionClick: (Int) -> Unit
) {
    val collections by viewModel.collections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf<Collection?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Collections") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Collection")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            if (collections.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No collections found. Tap the + button to add one.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(collections, key = { it.id }) { collection ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.deleteCollection(collection)
                                    return@rememberSwipeToDismissBoxState true
                                }
                                false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.animateItem(),
                            backgroundContent = {
                                Card(modifier = Modifier.padding(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            },
                            content = {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .combinedClickable(
                                            onClick = { onCollectionClick(collection.id) },
                                            onLongClick = { showEditDialog = collection }
                                        )
                                ) {
                                    Text(
                                        text = collection.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddCollectionDialog(
                onDismiss = { showAddDialog = false },
                onAdd = {
                    viewModel.addCollection(it)
                    showAddDialog = false
                }
            )
        }

        showEditDialog?.let { collection ->
            EditCollectionDialog(
                collection = collection,
                onDismiss = { showEditDialog = null },
                onSave = {
                    viewModel.updateCollection(it)
                    showEditDialog = null
                }
            )
        }
    }
}

@Composable
private fun AddCollectionDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Collection Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditCollectionDialog(
    collection: Collection,
    onDismiss: () -> Unit,
    onSave: (Collection) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(collection.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Collection Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(collection.copy(name = name)) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

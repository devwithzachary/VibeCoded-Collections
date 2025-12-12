package com.devwithzachary.collections.ui.collectiondetails

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.devwithzachary.collections.data.CollectionItem
import com.devwithzachary.collections.ui.collections.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val collectionItems by viewModel.collectionItems.collectAsState()
    val state by viewModel.state.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf<CollectionItem?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Collection Items") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort A-Z") },
                            onClick = {
                                viewModel.onSortTypeChange(SortType.ALPHABETICAL)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort Z-A") },
                            onClick = {
                                viewModel.onSortTypeChange(SortType.ALPHABETICAL_REVERSE)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter Have") },
                            onClick = {
                                viewModel.onFilterTypeChange(FilterType.HAVE)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter Want") },
                            onClick = {
                                viewModel.onFilterTypeChange(FilterType.WANT)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Filter") },
                            onClick = {
                                viewModel.onFilterTypeChange(FilterType.NONE)
                                showMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Collection Item")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            if (collectionItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No items match your search/filter.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(collectionItems, key = { it.id }) { item ->
                        val dismissState = rememberDismissState(
                            confirmValueChange = {
                                if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                                    viewModel.deleteItem(item)
                                    return@rememberDismissState true
                                }
                                false
                            }
                        )
                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier.animateItemPlacement(),
                            background = {
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
                            dismissContent = {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .combinedClickable(
                                            onClick = { showEditDialog = item },
                                            onLongClick = { showEditDialog = item }
                                        )
                                ) {
                                    ListItem(
                                        headlineContent = { Text(item.name) },
                                        supportingContent = { Text(item.description) },
                                        leadingContent = {
                                            AsyncImage(
                                                model = item.imageUri,
                                                contentDescription = item.name,
                                                modifier = Modifier.size(64.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        },
                                        trailingContent = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                    checked = item.has,
                                                    onCheckedChange = { viewModel.updateItem(item.copy(has = it)) }
                                                )
                                                Text("Have")
                                                Checkbox(
                                                    checked = item.wants,
                                                    onCheckedChange = { viewModel.updateItem(item.copy(wants = it)) }
                                                )
                                                Text("Want")
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            EditItemDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, description, has, wants, imageUri ->
                    viewModel.addItem(name, description, has, wants, imageUri)
                    showAddDialog = false
                }
            )
        }

        showEditDialog?.let { item ->
            EditItemDialog(
                item = item,
                onDismiss = { showEditDialog = null },
                onSave = { name, description, has, wants, imageUri ->
                    viewModel.updateItem(
                        item.copy(
                            name = name,
                            description = description,
                            has = has,
                            wants = wants,
                            imageUri = imageUri
                        )
                    )
                    showEditDialog = null
                }
            )
        }
    }
}

@Composable
private fun EditItemDialog(
    item: CollectionItem? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, has: Boolean, wants: Boolean, imageUri: String?) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(item?.name ?: "") }
    var description by rememberSaveable { mutableStateOf(item?.description ?: "") }
    var has by rememberSaveable { mutableStateOf(item?.has ?: false) }
    var wants by rememberSaveable { mutableStateOf(item?.wants ?: false) }
    var imageUri by rememberSaveable { mutableStateOf(item?.imageUri) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { imageUri = it.toString() } }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add New Item" else "Edit Item") },
        text = {
            Column {
                AsyncImage(
                    model = imageUri,
                    contentDescription = name,
                    modifier = Modifier.size(128.dp).align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
                Button(onClick = {
                    imagePicker.launch(
                        PickVisualMedia.Request(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Select Image")
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Item Description") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = has, onCheckedChange = { has = it })
                    Text("Have")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = wants, onCheckedChange = { wants = it })
                    Text("Want")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, description, has, wants, imageUri) },
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

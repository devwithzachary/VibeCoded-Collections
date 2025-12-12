package com.devwithzachary.collections.ui.collectiondetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devwithzachary.collections.ui.collections.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val collectionItems by viewModel.collectionItems.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var itemName by rememberSaveable { mutableStateOf("") }
    var itemDescription by rememberSaveable { mutableStateOf("") }
    var haveItem by rememberSaveable { mutableStateOf(false) }
    var wantItem by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Collection Items") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Collection Item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(collectionItems) { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                    supportingContent = { Text(item.description) },
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

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add New Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") }
                        )
                        OutlinedTextField(
                            value = itemDescription,
                            onValueChange = { itemDescription = it },
                            label = { Text("Item Description") }
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = haveItem, onCheckedChange = { haveItem = it })
                            Text("Have")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = wantItem, onCheckedChange = { wantItem = it })
                            Text("Want")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addItem(
                                name = itemName,
                                description = itemDescription,
                                has = haveItem,
                                wants = wantItem
                            )
                            itemName = ""
                            itemDescription = ""
                            haveItem = false
                            wantItem = false
                            showDialog = false
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

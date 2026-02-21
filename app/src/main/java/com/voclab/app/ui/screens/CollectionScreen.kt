package com.voclab.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voclab.app.data.db.VocabEntry
import com.voclab.app.ui.viewmodel.CollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(viewModel: CollectionViewModel, onNavigateBack: () -> Unit) {
    val collectionNames by viewModel.collectionNames.collectAsState()
    val selectedCollection by viewModel.selectedCollection.collectAsState()
    val entries by viewModel.entries.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedCollection ?: "Sammlungen")
                },
                navigationIcon = {
                    if (selectedCollection != null) {
                        IconButton(onClick = { viewModel.selectCollection("") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedCollection == null) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Neue Sammlung")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedCollection == null || selectedCollection!!.isEmpty()) {
                CollectionListView(
                    collectionNames = collectionNames,
                    onCollectionClick = { viewModel.selectCollection(it) }
                )
            } else {
                EntryListView(
                    entries = entries,
                    onDeleteEntry = { viewModel.deleteEntry(it) }
                )
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false; newCollectionName = "" },
            title = { Text("Neue Sammlung") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Name der Sammlung") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            viewModel.selectCollection(newCollectionName)
                            showCreateDialog = false
                            newCollectionName = ""
                        }
                    },
                    enabled = newCollectionName.isNotBlank()
                ) {
                    Text("Erstellen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false; newCollectionName = "" }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Composable
private fun CollectionListView(
    collectionNames: List<String>,
    onCollectionClick: (String) -> Unit
) {
    if (collectionNames.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Sammlungen vorhanden.\nErstelle eine neue Sammlung mit +", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(collectionNames) { name ->
                ListItem(
                    headlineContent = { Text(name) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { onCollectionClick(name) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun EntryListView(
    entries: List<VocabEntry>,
    onDeleteEntry: (VocabEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Vokabeln in dieser Sammlung.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(entries) { entry ->
                ListItem(
                    headlineContent = { Text("${entry.germanWord} → ${entry.translatedWord}") },
                    supportingContent = { Text(entry.targetLanguageLabel) },
                    trailingContent = {
                        IconButton(onClick = { onDeleteEntry(entry) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider()
            }
        }
    }
}

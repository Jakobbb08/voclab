package com.voclab.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voclab.app.ui.components.LanguageDropdown
import com.voclab.app.ui.components.TranslationResultCard
import com.voclab.app.ui.viewmodel.Language
import com.voclab.app.ui.viewmodel.TranslateUiState
import com.voclab.app.ui.viewmodel.TranslateViewModel
import com.voclab.app.ui.viewmodel.supportedLanguages
import kotlinx.coroutines.launch

@Composable
fun TranslateScreen(viewModel: TranslateViewModel, onNavigateToCollections: () -> Unit) {
    var inputWord by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages.first()) }
    val uiState by viewModel.uiState.collectAsState()
    val savedMessage by viewModel.savedMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    var existingCollections by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentTranslation by remember { mutableStateOf("") }

    LaunchedEffect(savedMessage) {
        savedMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSavedMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarContent(onNavigateToCollections = onNavigateToCollections)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputWord,
                onValueChange = { inputWord = it },
                label = { Text("Deutsches Wort eingeben...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            LanguageDropdown(
                selectedLanguage = selectedLanguage,
                languages = supportedLanguages,
                onLanguageSelected = { selectedLanguage = it }
            )

            Button(
                onClick = { viewModel.translate(inputWord, selectedLanguage) },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputWord.isNotBlank() && uiState !is TranslateUiState.Loading
            ) {
                Text("Übersetzen")
            }

            when (val state = uiState) {
                is TranslateUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TranslateUiState.Success -> {
                    currentTranslation = state.translatedText
                    TranslationResultCard(translatedText = state.translatedText)
                    Button(
                        onClick = {
                            scope.launch {
                                existingCollections = viewModel.getCollectionNames()
                                showSaveDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zu Sammlung hinzufügen")
                    }
                }
                is TranslateUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }
        }
    }

    if (showSaveDialog) {
        SaveToCollectionDialog(
            existingCollections = existingCollections,
            newCollectionName = newCollectionName,
            onNewCollectionNameChange = { newCollectionName = it },
            onSave = { collectionName ->
                viewModel.saveToCollection(
                    collectionName = collectionName,
                    germanWord = inputWord,
                    translatedWord = currentTranslation,
                    targetLanguage = selectedLanguage
                )
                showSaveDialog = false
                newCollectionName = ""
            },
            onDismiss = {
                showSaveDialog = false
                newCollectionName = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarContent(onNavigateToCollections: () -> Unit) {
    TopAppBar(
        title = { Text("VocLab") },
        actions = {
            TextButton(onClick = onNavigateToCollections) {
                Text("Sammlungen")
            }
        }
    )
}

@Composable
private fun SaveToCollectionDialog(
    existingCollections: List<String>,
    newCollectionName: String,
    onNewCollectionNameChange: (String) -> Unit,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zu Sammlung hinzufügen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (existingCollections.isNotEmpty()) {
                    Text("Bestehende Sammlungen:")
                    existingCollections.forEach { name ->
                        TextButton(
                            onClick = { onSave(name) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(name)
                        }
                    }
                    HorizontalDivider()
                }
                Text("Neue Sammlung erstellen:")
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = onNewCollectionNameChange,
                    label = { Text("Name der Sammlung") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (newCollectionName.isNotBlank()) onSave(newCollectionName) },
                enabled = newCollectionName.isNotBlank()
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

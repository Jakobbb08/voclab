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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(viewModel: TranslateViewModel) {
    var inputWord by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages.first()) }
    val uiState by viewModel.uiState.collectAsState()
    val ankiMessage by viewModel.ankiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(ankiMessage) {
        ankiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAnkiMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("VocLab") })
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
                    TranslationResultCard(translatedText = state.translatedText)
                    Button(
                        onClick = {
                            viewModel.addToAnkiDroid(state.originalWord, state.translatedText)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zu AnkiDroid hinzufügen")
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
}

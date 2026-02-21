package com.voclab.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voclab.app.data.db.VocabEntry
import com.voclab.app.data.repository.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Language(val code: String, val label: String)

val supportedLanguages = listOf(
    Language("es", "Spanisch"),
    Language("fr", "Französisch"),
    Language("en", "Englisch"),
    Language("it", "Italienisch"),
    Language("pt", "Portugiesisch")
)

sealed class TranslateUiState {
    object Idle : TranslateUiState()
    object Loading : TranslateUiState()
    data class Success(val translatedText: String) : TranslateUiState()
    data class Error(val message: String) : TranslateUiState()
}

class TranslateViewModel(private val repository: TranslationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<TranslateUiState>(TranslateUiState.Idle)
    val uiState: StateFlow<TranslateUiState> = _uiState

    private val _savedMessage = MutableStateFlow<String?>(null)
    val savedMessage: StateFlow<String?> = _savedMessage

    fun translate(word: String, targetLanguage: Language) {
        if (word.isBlank()) return
        viewModelScope.launch {
            _uiState.value = TranslateUiState.Loading
            val result = repository.translate(word, "de|${targetLanguage.code}")
            result.onSuccess { response ->
                _uiState.value = TranslateUiState.Success(response.responseData.translatedText)
            }.onFailure {
                _uiState.value = TranslateUiState.Error("Keine Verbindung oder Übersetzung nicht gefunden.")
            }
        }
    }

    fun saveToCollection(
        collectionName: String,
        germanWord: String,
        translatedWord: String,
        targetLanguage: Language
    ) {
        viewModelScope.launch {
            val entry = VocabEntry(
                collectionName = collectionName,
                germanWord = germanWord,
                translatedWord = translatedWord,
                targetLanguage = targetLanguage.code,
                targetLanguageLabel = targetLanguage.label
            )
            repository.addVocabEntry(entry)
            _savedMessage.value = "Gespeichert in „$collectionName""
        }
    }

    fun clearSavedMessage() {
        _savedMessage.value = null
    }

    suspend fun getCollectionNames(): List<String> = repository.getAllCollectionNamesList()

    class Factory(private val repository: TranslationRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TranslateViewModel(repository) as T
        }
    }
}

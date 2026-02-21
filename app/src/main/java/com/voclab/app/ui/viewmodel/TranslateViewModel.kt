package com.voclab.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voclab.app.data.anki.AnkiDroidHelper
import com.voclab.app.data.repository.TranslationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    data class Success(val originalWord: String, val translatedText: String) : TranslateUiState()
    data class Error(val message: String) : TranslateUiState()
}

class TranslateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TranslationRepository()

    private val _uiState = MutableStateFlow<TranslateUiState>(TranslateUiState.Idle)
    val uiState: StateFlow<TranslateUiState> = _uiState

    private val _ankiMessage = MutableStateFlow<String?>(null)
    val ankiMessage: StateFlow<String?> = _ankiMessage

    fun translate(word: String, targetLanguage: Language) {
        if (word.isBlank()) return
        viewModelScope.launch {
            _uiState.value = TranslateUiState.Loading
            val result = repository.translate(word, "de|${targetLanguage.code}")
            result.onSuccess { response ->
                _uiState.value = TranslateUiState.Success(
                    originalWord = word,
                    translatedText = response.responseData.translatedText
                )
            }.onFailure {
                _uiState.value = TranslateUiState.Error("Keine Verbindung oder Übersetzung nicht gefunden.")
            }
        }
    }

    fun addToAnkiDroid(germanWord: String, translatedWord: String) {
        val context = getApplication<Application>()
        if (!AnkiDroidHelper.isAnkiDroidInstalled(context)) {
            _ankiMessage.value = "AnkiDroid ist nicht installiert."
            return
        }
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                AnkiDroidHelper.addNote(context, germanWord, translatedWord)
            }
            _ankiMessage.value = error ?: "Karte zu AnkiDroid hinzugefügt."
        }
    }

    fun clearAnkiMessage() {
        _ankiMessage.value = null
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TranslateViewModel(application) as T
        }
    }
}

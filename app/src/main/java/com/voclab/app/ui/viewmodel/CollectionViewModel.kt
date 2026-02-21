package com.voclab.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voclab.app.data.db.VocabEntry
import com.voclab.app.data.repository.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(private val repository: TranslationRepository) : ViewModel() {

    private val _collectionNames = MutableStateFlow<List<String>>(emptyList())
    val collectionNames: StateFlow<List<String>> = _collectionNames

    init {
        viewModelScope.launch {
            repository.getAllCollectionNames().collect { names ->
                _collectionNames.value = names
            }
        }
    }

    private val _selectedCollection = MutableStateFlow<String?>(null)
    val selectedCollection: StateFlow<String?> = _selectedCollection

    private val _entries = MutableStateFlow<List<VocabEntry>>(emptyList())
    val entries: StateFlow<List<VocabEntry>> = _entries

    fun selectCollection(name: String) {
        if (name.isEmpty()) {
            _selectedCollection.value = null
            _entries.value = emptyList()
            return
        }
        _selectedCollection.value = name
        viewModelScope.launch {
            repository.getEntriesForCollection(name).collect { list ->
                _entries.value = list
            }
        }
    }

    fun deleteEntry(entry: VocabEntry) {
        viewModelScope.launch {
            repository.deleteVocabEntry(entry)
        }
    }

    class Factory(private val repository: TranslationRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CollectionViewModel(repository) as T
        }
    }
}

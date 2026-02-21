package com.voclab.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab_entries")
data class VocabEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collectionName: String,
    val germanWord: String,
    val translatedWord: String,
    val targetLanguage: String,
    val targetLanguageLabel: String
)

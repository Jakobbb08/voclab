package com.voclab.app.data.repository

import com.voclab.app.data.api.RetrofitInstance
import com.voclab.app.data.api.TranslationResponse
import com.voclab.app.data.db.VocabDao
import com.voclab.app.data.db.VocabEntry
import kotlinx.coroutines.flow.Flow

class TranslationRepository(private val vocabDao: VocabDao) {

    suspend fun translate(word: String, langPair: String): Result<TranslationResponse> {
        return try {
            val response = RetrofitInstance.api.translate(word, langPair)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllCollectionNames(): Flow<List<String>> = vocabDao.getAllCollectionNames()

    fun getEntriesForCollection(name: String): Flow<List<VocabEntry>> =
        vocabDao.getEntriesForCollection(name)

    suspend fun addVocabEntry(entry: VocabEntry) = vocabDao.insert(entry)

    suspend fun deleteVocabEntry(entry: VocabEntry) = vocabDao.delete(entry)

    suspend fun getAllCollectionNamesList(): List<String> = vocabDao.getAllCollectionNamesList()
}

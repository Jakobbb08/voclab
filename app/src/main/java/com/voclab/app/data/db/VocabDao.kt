package com.voclab.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT DISTINCT collectionName FROM vocab_entries ORDER BY collectionName ASC")
    fun getAllCollectionNames(): Flow<List<String>>

    @Query("SELECT * FROM vocab_entries WHERE collectionName = :name ORDER BY id ASC")
    fun getEntriesForCollection(name: String): Flow<List<VocabEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VocabEntry)

    @Delete
    suspend fun delete(entry: VocabEntry)

    @Query("SELECT DISTINCT collectionName FROM vocab_entries ORDER BY collectionName ASC")
    suspend fun getAllCollectionNamesList(): List<String>
}

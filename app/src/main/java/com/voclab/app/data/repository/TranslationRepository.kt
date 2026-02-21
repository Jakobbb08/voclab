package com.voclab.app.data.repository

import com.voclab.app.data.api.RetrofitInstance
import com.voclab.app.data.api.TranslationResponse

class TranslationRepository {

    suspend fun translate(word: String, langPair: String): Result<TranslationResponse> {
        return try {
            val response = RetrofitInstance.api.translate(word, langPair)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

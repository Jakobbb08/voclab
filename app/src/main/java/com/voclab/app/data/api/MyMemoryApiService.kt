package com.voclab.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MyMemoryApiService {
    @GET("get")
    suspend fun translate(
        @Query("q") word: String,
        @Query("langpair") langPair: String
    ): TranslationResponse
}

object RetrofitInstance {
    val api: MyMemoryApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mymemory.translated.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyMemoryApiService::class.java)
    }
}

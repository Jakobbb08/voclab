package com.voclab.app.data.api

data class TranslationResponse(
    val responseData: ResponseData,
    val responseStatus: Int
)

data class ResponseData(
    val translatedText: String
)

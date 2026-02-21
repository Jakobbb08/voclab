package com.voclab.app.data.anki

import android.content.Context
import com.ichi2.anki.api.AddContentApi

object AnkiDroidHelper {

    private const val DECK_NAME = "VocLab"
    private const val MODEL_NAME = "Basic"

    fun isAnkiDroidInstalled(context: Context): Boolean =
        AddContentApi.getAnkiDroidPackageName(context) != null

    /**
     * Adds a note to AnkiDroid with the German word as the front
     * and the translation as the back. Uses the "VocLab" deck and
     * the built-in "Basic" model (Front / Back fields).
     *
     * @return null on success, or a human-readable error message on failure
     */
    fun addNote(context: Context, germanWord: String, translatedWord: String): String? {
        val api = AddContentApi(context)
        val deckId = api.findDeckIdByName(DECK_NAME) ?: api.addNewDeck(DECK_NAME)
            ?: return "Deck „$DECK_NAME" konnte nicht erstellt werden."
        val modelId = api.findModelIdByName(MODEL_NAME)
            ?: return "Das Notiztyp „$MODEL_NAME" wurde in AnkiDroid nicht gefunden."
        val fields = arrayOf(germanWord, translatedWord)
        return if (api.addNote(modelId, deckId, fields, null) != null) null
        else "Die Karte konnte nicht zu AnkiDroid hinzugefügt werden."
    }
}

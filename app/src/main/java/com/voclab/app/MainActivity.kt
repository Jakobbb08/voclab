package com.voclab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.voclab.app.data.db.AppDatabase
import com.voclab.app.data.repository.TranslationRepository
import com.voclab.app.navigation.AppNavGraph
import com.voclab.app.ui.theme.VocLabTheme
import com.voclab.app.ui.viewmodel.CollectionViewModel
import com.voclab.app.ui.viewmodel.TranslateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = TranslationRepository(database.vocabDao())

        val translateViewModel = ViewModelProvider(
            this,
            TranslateViewModel.Factory(repository)
        )[TranslateViewModel::class.java]

        val collectionViewModel = ViewModelProvider(
            this,
            CollectionViewModel.Factory(repository)
        )[CollectionViewModel::class.java]

        setContent {
            VocLabTheme {
                AppNavGraph(
                    translateViewModel = translateViewModel,
                    collectionViewModel = collectionViewModel
                )
            }
        }
    }
}

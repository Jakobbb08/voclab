package com.voclab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.voclab.app.ui.screens.TranslateScreen
import com.voclab.app.ui.theme.VocLabTheme
import com.voclab.app.ui.viewmodel.TranslateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val translateViewModel = ViewModelProvider(
            this,
            TranslateViewModel.Factory(application)
        )[TranslateViewModel::class.java]

        setContent {
            VocLabTheme {
                TranslateScreen(viewModel = translateViewModel)
            }
        }
    }
}

package com.voclab.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voclab.app.ui.screens.CollectionScreen
import com.voclab.app.ui.screens.TranslateScreen
import com.voclab.app.ui.viewmodel.CollectionViewModel
import com.voclab.app.ui.viewmodel.TranslateViewModel

object Routes {
    const val TRANSLATE = "translate"
    const val COLLECTIONS = "collections"
}

@Composable
fun AppNavGraph(
    translateViewModel: TranslateViewModel,
    collectionViewModel: CollectionViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.TRANSLATE) {
        composable(Routes.TRANSLATE) {
            TranslateScreen(
                viewModel = translateViewModel,
                onNavigateToCollections = { navController.navigate(Routes.COLLECTIONS) }
            )
        }
        composable(Routes.COLLECTIONS) {
            CollectionScreen(
                viewModel = collectionViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

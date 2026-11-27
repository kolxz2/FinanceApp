@file:Suppress("FunctionNaming")

package com.example.financeapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.ui.editor.EditorScreen
import com.example.financeapp.ui.editor.EditorStatus
import com.example.financeapp.ui.editor.EditorViewModel
import com.example.financeapp.ui.history.HistoryScreen
import com.example.financeapp.ui.history.HistoryViewModel

private const val HISTORY_ROUTE = "history"
private const val EDITOR_ROUTE = "editor"

@Suppress("MagicNumber")
private val lightColors = lightColorScheme(
    primary = Color(0xFF245849),
    primaryContainer = Color(0xFFDCEEE4),
    onPrimaryContainer = Color(0xFF17382E),
    background = Color(0xFFF9FAF6),
    surface = Color(0xFFF9FAF6),
)

@Suppress("MagicNumber")
private val darkColors = darkColorScheme(
    primary = Color(0xFFA9D4BE),
    primaryContainer = Color(0xFF254B3E),
    onPrimaryContainer = Color(0xFFDCEEE4),
)

@Composable
internal fun FinanceApp(repository: TransactionRepository) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors) {
        val navController = rememberNavController()
        val factory = viewModelFactory {
            initializer { HistoryViewModel(repository) }
            initializer { EditorViewModel(repository, createSavedStateHandle()) }
        }
        NavHost(navController, startDestination = HISTORY_ROUTE) {
            composable(HISTORY_ROUTE) {
                val viewModel: HistoryViewModel = viewModel(factory = factory)
                val state by viewModel.state.collectAsStateWithLifecycle()
                HistoryScreen(state, viewModel::onIntent) {
                    navController.navigate(EDITOR_ROUTE) { launchSingleTop = true }
                }
            }
            composable(EDITOR_ROUTE) {
                val viewModel: EditorViewModel = viewModel(factory = factory)
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(state.status) {
                    if (state.status == EditorStatus.SAVED) navController.popBackStack(EDITOR_ROUTE, inclusive = true)
                }
                EditorScreen(state, viewModel::onIntent) {
                    navController.popBackStack(EDITOR_ROUTE, inclusive = true)
                }
            }
        }
    }
}

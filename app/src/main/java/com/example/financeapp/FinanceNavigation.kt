package com.example.financeapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val TRANSACTIONS_ROUTE = "transactions"
private const val SETTINGS_ROUTE = "settings"

@Composable
@Suppress("FunctionNaming")
internal fun FinanceNavigation() {
    val navController = rememberNavController()
    val transactionViewModel: TransactionViewModel = viewModel()
    var showBalance by rememberSaveable { mutableStateOf(true) }

    NavHost(navController = navController, startDestination = TRANSACTIONS_ROUTE) {
        composable(TRANSACTIONS_ROUTE) {
            TransactionScreen(
                viewModel = transactionViewModel,
                showBalance = showBalance,
                onOpenSettings = {
                    navController.navigate(SETTINGS_ROUTE) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                showBalance = showBalance,
                onShowBalanceChange = { showBalance = it },
                onBack = { navController.navigateUp() },
            )
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun SettingsScreen(showBalance: Boolean, onShowBalanceChange: (Boolean) -> Unit, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = showBalance, role = Role.Switch, onValueChange = onShowBalanceChange),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.settings_show_balance), modifier = Modifier.weight(1f))
                Switch(checked = showBalance, onCheckedChange = null)
            }
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.settings_back))
            }
        }
    }
}

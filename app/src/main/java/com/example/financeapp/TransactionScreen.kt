package com.example.financeapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(viewModel: TransactionViewModel, showBalance: Boolean, onOpenSettings: () -> Unit) {
    val state = viewModel.uiState
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(stringResource(R.string.transaction_title))
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.settings_title))
            }
            if (showBalance) {
                Text(stringResource(R.string.balance_summary, state.balance, state.transactionCount))
            } else {
                Text(stringResource(R.string.transaction_count, state.transactionCount))
            }
            if (state.isLoading) {
                Text(stringResource(R.string.transaction_loading))
            }
            TransactionForm(viewModel, state)
            TransactionHistory(state.transactions)
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionForm(viewModel: TransactionViewModel, state: MainUiState) {
    val enabled = state.isDatabaseReady && !state.isSaving
    TransactionInputs(state, enabled, viewModel::onEvent)
    state.error?.let { error ->
        val textId = when (error) {
            TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
            TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
            TransactionError.DATABASE_ERROR -> R.string.transaction_database_error
        }
        Text(
            stringResource(textId),
            color = MaterialTheme.colorScheme.error,
        )
    }
    Button(onClick = viewModel::addTransaction, enabled = enabled) {
        Text(stringResource(R.string.transaction_add))
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionInputs(state: MainUiState, enabled: Boolean, onEvent: (MainUiEvent) -> Unit) {
    OutlinedTextField(
        value = state.description,
        onValueChange = {
            onEvent(MainUiEvent.DescriptionChanged(it))
        },
        label = { Text(stringResource(R.string.transaction_description)) },
        enabled = enabled,
    )
    OutlinedTextField(
        value = state.amount,
        onValueChange = {
            onEvent(MainUiEvent.AmountChanged(it))
        },
        label = { Text(stringResource(R.string.transaction_amount)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = enabled,
    )
    Row {
        FilterChip(
            selected = state.type == TransactionType.EXPENSE,
            onClick = {
                onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE))
            },
            label = { Text(stringResource(R.string.transaction_expense)) },
            enabled = enabled,
        )
        FilterChip(
            selected = state.type == TransactionType.INCOME,
            onClick = {
                onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME))
            },
            label = { Text(stringResource(R.string.transaction_income)) },
            enabled = enabled,
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionHistory(transactions: List<Transaction>) {
    if (transactions.isNotEmpty()) {
        Text(stringResource(R.string.transaction_history))
        transactions.asReversed().forEach { tx ->
            val row = if (tx.type == TransactionType.INCOME) {
                R.string.transaction_history_income
            } else {
                R.string.transaction_history_expense
            }
            Text(stringResource(row, tx.description, tx.amount))
        }
    }
}

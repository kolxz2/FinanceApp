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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(viewModel: TransactionViewModel = viewModel()) {
    val state = viewModel.uiState
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.transaction_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.balance_summary, state.balance, state.transactionCount))
            if (state.isLoading) {
                Text(stringResource(R.string.transaction_loading))
            }
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(MainUiEvent.DescriptionChanged(it)) },
                label = { Text(stringResource(R.string.transaction_description)) },
                singleLine = true,
                enabled = state.isDatabaseReady && !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(MainUiEvent.AmountChanged(it)) },
                label = { Text(stringResource(R.string.transaction_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = state.isDatabaseReady && !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = state.type == TransactionType.EXPENSE,
                    enabled = state.isDatabaseReady && !state.isSaving,
                    onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE)) },
                    label = { Text(stringResource(R.string.transaction_expense)) },
                )
                FilterChip(
                    selected = state.type == TransactionType.INCOME,
                    enabled = state.isDatabaseReady && !state.isSaving,
                    onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME)) },
                    label = { Text(stringResource(R.string.transaction_income)) },
                )
            }
            state.error?.let { error ->
                val message = when (error) {
                    TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
                    TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
                    TransactionError.DATABASE_ERROR -> R.string.transaction_database_error
                }
                Text(stringResource(message), color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = viewModel::addTransaction,
                enabled = state.isDatabaseReady && !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.transaction_add))
            }
            TransactionHistory(state.transactions)
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionHistory(transactions: List<Transaction>) {
    if (transactions.isNotEmpty()) {
        Text(stringResource(R.string.transaction_history), style = MaterialTheme.typography.titleLarge)
        transactions.asReversed().forEach { transaction ->
            val row = if (transaction.type == TransactionType.INCOME) {
                R.string.transaction_history_income
            } else {
                R.string.transaction_history_expense
            }
            Text(stringResource(row, transaction.description, transaction.amount))
        }
    }
}

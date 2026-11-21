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

private val screenPadding = 24.dp
private val itemSpacing = 12.dp
private val chipSpacing = 8.dp

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(viewModel: TransactionViewModel, showBalance: Boolean, onOpenSettings: () -> Unit) {
    val state = viewModel.uiState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(screenPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            TransactionHeader(
                state = state,
                showBalance = showBalance,
                onOpenSettings = onOpenSettings,
            )
            TransactionForm(viewModel = viewModel, state = state)
            TransactionHistory(transactions = state.transactions)
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionHeader(state: MainUiState, showBalance: Boolean, onOpenSettings: () -> Unit) {
    Text(
        text = stringResource(R.string.transaction_title),
        style = MaterialTheme.typography.headlineSmall,
    )
    TextButton(onClick = onOpenSettings) {
        Text(text = stringResource(R.string.settings_title))
    }
    TransactionBalance(state = state, showBalance = showBalance)
    if (state.isLoading) {
        Text(text = stringResource(R.string.transaction_loading))
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionBalance(state: MainUiState, showBalance: Boolean) {
    val summaryResource = if (showBalance) {
        R.string.balance_summary
    } else {
        R.string.transaction_count
    }
    if (showBalance) {
        Text(
            text = stringResource(summaryResource, state.balance, state.transactionCount),
            style = MaterialTheme.typography.headlineSmall,
        )
    } else {
        Text(
            text = stringResource(summaryResource, state.transactionCount),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionForm(viewModel: TransactionViewModel, state: MainUiState) {
    val isEnabled = state.isDatabaseReady && !state.isSaving

    TransactionInputFields(
        state = state,
        enabled = isEnabled,
        onAmountChanged = { value ->
            viewModel.onEvent(MainUiEvent.AmountChanged(value))
        },
        onDescriptionChanged = { value ->
            viewModel.onEvent(MainUiEvent.DescriptionChanged(value))
        },
    )
    TransactionTypeSelector(
        selectedType = state.type,
        enabled = isEnabled,
        onTypeChanged = { type ->
            viewModel.onEvent(MainUiEvent.TypeChanged(type))
        },
    )
    TransactionErrorMessage(error = state.error)
    Button(
        onClick = viewModel::addTransaction,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.transaction_add))
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionInputFields(
    state: MainUiState,
    enabled: Boolean,
    onAmountChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = state.description,
        onValueChange = onDescriptionChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.transaction_description)) },
        singleLine = true,
        enabled = enabled,
    )
    OutlinedTextField(
        value = state.amount,
        onValueChange = onAmountChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.transaction_amount)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        enabled = enabled,
    )
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    enabled: Boolean,
    onTypeChanged: (TransactionType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(chipSpacing)) {
        FilterChip(
            selected = selectedType == TransactionType.EXPENSE,
            enabled = enabled,
            onClick = { onTypeChanged(TransactionType.EXPENSE) },
            label = { Text(stringResource(R.string.transaction_expense)) },
        )
        FilterChip(
            selected = selectedType == TransactionType.INCOME,
            enabled = enabled,
            onClick = { onTypeChanged(TransactionType.INCOME) },
            label = { Text(stringResource(R.string.transaction_income)) },
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionErrorMessage(error: TransactionError?) {
    error?.let { transactionError ->
        val errorResource = when (transactionError) {
            TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
            TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
            TransactionError.DATABASE_ERROR -> R.string.transaction_database_error
        }
        Text(
            text = stringResource(errorResource),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionHistory(transactions: List<Transaction>) {
    if (transactions.isNotEmpty()) {
        Text(
            text = stringResource(R.string.transaction_history),
            style = MaterialTheme.typography.titleMedium,
        )
        transactions.asReversed().forEach { transaction ->
            val rowResource = when (transaction.type) {
                TransactionType.INCOME -> R.string.transaction_history_income
                TransactionType.EXPENSE -> R.string.transaction_history_expense
            }
            Text(
                text = stringResource(
                    rowResource,
                    transaction.description,
                    transaction.amount,
                ),
            )
        }
    }
}

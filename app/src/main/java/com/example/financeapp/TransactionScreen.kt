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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(
    viewModel: TransactionViewModel = viewModel(),
) {
    val state = viewModel.uiState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.transaction_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.transactionCount,
                ),
                style = MaterialTheme.typography.titleMedium,
            )
            TransactionForm(
                state = state,
                onEvent = viewModel::onEvent,
                onAdd = viewModel::addTransaction,
            )
            TransactionHistory(transactions = state.transactions)
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionForm(
    state: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    onAdd: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
    ) {
        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(MainUiEvent.DescriptionChanged(it)) },
            label = { Text(stringResource(R.string.transaction_description)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.amount,
            onValueChange = { onEvent(MainUiEvent.AmountChanged(it)) },
            label = { Text(stringResource(R.string.transaction_amount)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
            FilterChip(
                selected = state.type == TransactionType.EXPENSE,
                onClick = { onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE)) },
                label = { Text(stringResource(R.string.transaction_expense)) },
            )
            FilterChip(
                selected = state.type == TransactionType.INCOME,
                onClick = { onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME)) },
                label = { Text(stringResource(R.string.transaction_income)) },
            )
        }
        state.error?.let { error ->
            val textId = when (error) {
                TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
                TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
            }
            Text(
                text = stringResource(textId),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.transaction_add))
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionHistory(transactions: List<Transaction>) {
    if (transactions.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
            Text(
                text = stringResource(R.string.transaction_history),
                style = MaterialTheme.typography.titleMedium,
            )
            transactions.asReversed().forEach { transaction ->
                val template = if (transaction.type == TransactionType.INCOME) {
                    R.string.transaction_history_income
                } else {
                    R.string.transaction_history_expense
                }
                Text(
                    text = stringResource(
                        template,
                        transaction.description,
                        transaction.amount,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("FunctionNaming", "UnusedPrivateFunction")
private fun TransactionFormPreview() {
    FinanceAppTheme {
        Surface {
            TransactionForm(
                state = MainUiState(
                    amount = "250",
                    description = "Кофе",
                    type = TransactionType.EXPENSE,
                ),
                onEvent = {},
                onAdd = {},
            )
        }
    }
}

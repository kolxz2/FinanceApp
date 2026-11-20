package com.example.financeapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val INCOME_AMOUNT = 500
private const val EXPENSE_AMOUNT = 250

@Composable
@Suppress("FunctionNaming")
internal fun FinanceAppScreen() {
    var state by remember { mutableStateOf(MainUiState()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Text(
                text = stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.transactionCount,
                ),
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(
                onClick = {
                    val event = MainUiEvent.TransactionAdded(
                        Transaction(TransactionType.INCOME, INCOME_AMOUNT),
                    )
                    state = reduce(state, event)
                },
            ) {
                Text(text = stringResource(R.string.add_income_button))
            }
            Button(
                onClick = {
                    val event = MainUiEvent.TransactionAdded(
                        Transaction(TransactionType.EXPENSE, EXPENSE_AMOUNT),
                    )
                    state = reduce(state, event)
                },
            ) {
                Text(text = stringResource(R.string.add_expense_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("FunctionNaming", "UnusedPrivateFunction")
private fun FinanceAppScreenPreview() {
    FinanceAppTheme {
        FinanceAppScreen()
    }
}

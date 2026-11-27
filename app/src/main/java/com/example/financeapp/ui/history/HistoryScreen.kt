@file:Suppress("FunctionNaming")
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.financeapp.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.R
import com.example.financeapp.domain.Transaction
import java.text.NumberFormat

@Composable
internal fun HistoryScreen(state: HistoryState, onIntent: (HistoryIntent) -> Unit, onAdd: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.history_title)) }) }) { padding ->
        val content = Modifier.fillMaxSize().padding(padding)
        when (state) {
            HistoryState.Loading -> Box(content, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            HistoryState.LoadFailed -> Box(content, contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.history_load_failed), textAlign = TextAlign.Center)
                    Button(onClick = { onIntent(HistoryIntent.RETRY) }) { Text(stringResource(R.string.retry)) }
                }
            }

            is HistoryState.Ready -> HistoryContent(state, onAdd, content)
        }
    }
}

@Composable
private fun HistoryContent(state: HistoryState.Ready, onAdd: () -> Unit, modifier: Modifier) {
    val formatter = remember { NumberFormat.getIntegerInstance() }
    LazyColumn(modifier, contentPadding = PaddingValues(20.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.balance), style = MaterialTheme.typography.labelLarge)
                    Text(
                        stringResource(R.string.ruble_amount, formatter.format(state.balance)),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp)) {
                Text(stringResource(R.string.add_transaction))
            }
            if (state.transactions.isEmpty()) {
                Text(stringResource(R.string.history_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(state.transactions, key = { it.id }) { transaction ->
            TransactionRow(transaction, formatter)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, formatter: NumberFormat) {
    val amount = transaction.signedAmount
    val amountLabel = if (amount > 0) R.string.ruble_income else R.string.ruble_amount
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(transaction.description, modifier = Modifier.weight(1f))
        Text(
            stringResource(amountLabel, formatter.format(amount)),
            color = if (amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@file:Suppress("FunctionNaming")
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.financeapp.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financeapp.R
import com.example.financeapp.domain.TransactionType

@Composable
internal fun EditorScreen(state: EditorState, onIntent: (EditorIntent) -> Unit, onBack: () -> Unit) {
    val canEdit = state.status != EditorStatus.SAVING && state.status != EditorStatus.SAVED
    BackHandler(enabled = !canEdit) { /* Keep the editor open until the write completes. */ }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.editor_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack, enabled = canEdit) { Text(stringResource(R.string.back)) }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TransactionFields(state, canEdit, onIntent)
            if (state.status == EditorStatus.SAVE_FAILED) {
                Text(stringResource(R.string.editor_save_failed), color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = { onIntent(EditorIntent.Save) },
                enabled = canEdit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val label = if (state.status == EditorStatus.SAVING) R.string.saving else R.string.save
                Text(stringResource(label))
            }
        }
    }
}

@Composable
private fun TransactionFields(state: EditorState, enabled: Boolean, onIntent: (EditorIntent) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TransactionType.entries.forEach { type ->
            FilterChip(
                selected = state.type == type,
                enabled = enabled,
                onClick = { onIntent(EditorIntent.ChangeType(type)) },
                label = {
                    Text(stringResource(if (type == TransactionType.INCOME) R.string.income else R.string.expense))
                },
            )
        }
    }
    val invalidAmount = state.status == EditorStatus.INVALID_AMOUNT
    OutlinedTextField(
        value = state.amount,
        onValueChange = { onIntent(EditorIntent.ChangeAmount(it)) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.amount)) },
        singleLine = true,
        isError = invalidAmount,
        supportingText = if (invalidAmount) ({ Text(stringResource(R.string.editor_invalid_amount)) }) else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
    val invalidDescription = state.status == EditorStatus.INVALID_DESCRIPTION
    OutlinedTextField(
        value = state.description,
        onValueChange = { onIntent(EditorIntent.ChangeDescription(it)) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.description)) },
        singleLine = true,
        isError = invalidDescription,
        supportingText = if (invalidDescription) {
            { Text(stringResource(R.string.editor_invalid_description)) }
        } else {
            null
        },
    )
}

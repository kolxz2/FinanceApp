package com.example.financeapp.ui.editor

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.domain.TransactionStorageException
import com.example.financeapp.domain.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val AMOUNT = "amount"
private const val DESCRIPTION = "description"
private const val TYPE = "type"
private const val SAVED = "saved"

internal data class EditorState(
    val description: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val status: EditorStatus = EditorStatus.EDITING,
)

internal enum class EditorStatus { EDITING, INVALID_AMOUNT, INVALID_DESCRIPTION, SAVING, SAVE_FAILED, SAVED }

internal sealed interface EditorIntent {
    data class ChangeDescription(val value: String) : EditorIntent
    data class ChangeAmount(val value: String) : EditorIntent
    data class ChangeType(val value: TransactionType) : EditorIntent
    data object Save : EditorIntent
}

internal class EditorViewModel(
    private val repository: TransactionRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        EditorState(
            description = savedState.get<String>(DESCRIPTION).orEmpty(),
            amount = savedState.get<String>(AMOUNT).orEmpty(),
            type = savedState.get<TransactionType>(TYPE) ?: TransactionType.EXPENSE,
            status = if (savedState.get<Boolean>(SAVED) == true) EditorStatus.SAVED else EditorStatus.EDITING,
        ),
    )
    val state = mutableState.asStateFlow()

    fun onIntent(intent: EditorIntent) {
        val current = state.value
        if (current.status == EditorStatus.SAVING || current.status == EditorStatus.SAVED) return

        when (intent) {
            is EditorIntent.ChangeDescription -> edit(current.copy(description = intent.value))
            is EditorIntent.ChangeAmount -> edit(current.copy(amount = intent.value))
            is EditorIntent.ChangeType -> edit(current.copy(type = intent.value))
            EditorIntent.Save -> save(current)
        }
    }

    private fun edit(next: EditorState) {
        savedState[DESCRIPTION] = next.description
        savedState[AMOUNT] = next.amount
        savedState[TYPE] = next.type
        mutableState.value = next.copy(status = EditorStatus.EDITING)
    }

    private fun save(current: EditorState) {
        val amount = current.amount.trim().toIntOrNull()
        val description = current.description.trim()
        when {
            amount == null || amount <= 0 -> mutableState.value = current.copy(status = EditorStatus.INVALID_AMOUNT)
            description.isEmpty() -> mutableState.value = current.copy(status = EditorStatus.INVALID_DESCRIPTION)
            else -> persist(current, Transaction(amount = amount, description = description, type = current.type))
        }
    }

    private fun persist(current: EditorState, transaction: Transaction) {
        mutableState.value = current.copy(status = EditorStatus.SAVING)
        viewModelScope.launch {
            try {
                repository.add(transaction)
                savedState[SAVED] = true
                mutableState.value = current.copy(status = EditorStatus.SAVED)
            } catch (error: TransactionStorageException) {
                Log.e("FinanceEditor", "Could not save a transaction", error)
                mutableState.value = current.copy(status = EditorStatus.SAVE_FAILED)
            }
        }
    }
}

package com.example.financeapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

internal class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TransactionRepository(application)

    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        repository.load { transactions ->
            uiState = if (transactions == null) {
                uiState.copy(isLoading = false, error = TransactionError.DATABASE_ERROR)
            } else {
                transactions.fold(MainUiState(isLoading = false, isDatabaseReady = true)) { state, transaction ->
                    reduce(state, MainUiEvent.AddTransaction(transaction))
                }
            }
        }
    }

    fun onEvent(event: MainUiEvent) {
        if (!uiState.isDatabaseReady || uiState.isSaving) return
        if (event is MainUiEvent.AddTransaction) {
            val nextState = reduce(uiState, event)
            if (nextState.error != null) {
                uiState = nextState
                return
            }
            uiState = uiState.copy(isSaving = true)
            repository.save(event.transaction) { saved ->
                uiState = if (saved) {
                    nextState
                } else {
                    uiState.copy(
                        isSaving = false,
                        error = TransactionError.DATABASE_ERROR,
                    )
                }
            }
        } else {
            uiState = reduce(uiState, event)
        }
    }

    fun addTransaction() {
        val description = uiState.description.trim()
        val amount = uiState.amount.toIntOrNull()
        val event = when {
            uiState.amount.isEmpty() || description.isEmpty() -> MainUiEvent.InvalidTransaction

            amount == null -> MainUiEvent.AmountTooLarge

            amount <= 0 -> MainUiEvent.InvalidTransaction

            else -> MainUiEvent.AddTransaction(
                Transaction(amount = amount, type = uiState.type, description = description),
            )
        }
        onEvent(event)
    }

    private fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
        is MainUiEvent.AmountChanged -> {
            if (event.value.all(Char::isDigit)) state.copy(amount = event.value, error = null) else state
        }

        is MainUiEvent.DescriptionChanged -> state.copy(description = event.value, error = null)

        is MainUiEvent.TypeChanged -> state.copy(type = event.type, error = null)

        MainUiEvent.InvalidTransaction -> state.copy(error = TransactionError.INVALID_INPUT)

        MainUiEvent.AmountTooLarge -> state.copy(error = TransactionError.AMOUNT_TOO_LARGE)

        is MainUiEvent.AddTransaction -> {
            val transaction = event.transaction
            require(transaction.amount > 0) { "Transaction amount must be positive" }
            require(transaction.description.isNotBlank()) { "Transaction description must not be blank" }

            val balanceChange = when (transaction.type) {
                TransactionType.INCOME -> transaction.amount
                TransactionType.EXPENSE -> -transaction.amount
            }
            val newBalance = state.balance.toLong() + balanceChange
            if (newBalance !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
                state.copy(error = TransactionError.AMOUNT_TOO_LARGE)
            } else {
                state.copy(
                    amount = "",
                    description = "",
                    balance = newBalance.toInt(),
                    transactionCount = state.transactionCount + 1,
                    transactions = state.transactions + transaction,
                    error = null,
                )
            }
        }
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }
}

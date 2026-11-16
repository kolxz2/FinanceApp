package com.example.financeapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

internal class TransactionViewModel : ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    fun onEvent(event: MainUiEvent) {
        uiState = reduce(uiState, event)
    }

    fun addTransaction() {
        val description = uiState.description.trim()
        val amount = uiState.amount.toIntOrNull()

        val event = when {
            uiState.amount.isEmpty() || description.isEmpty() ->
                MainUiEvent.InvalidTransaction

            amount == null -> MainUiEvent.AmountTooLarge

            amount <= 0 -> MainUiEvent.InvalidTransaction

            else -> MainUiEvent.AddTransaction(
                Transaction(
                    amount = amount,
                    type = uiState.type,
                    description = description,
                ),
            )
        }

        onEvent(event)
    }

    private fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
        is MainUiEvent.AmountChanged -> {
            if (event.value.all(Char::isDigit)) {
                state.copy(amount = event.value, error = null)
            } else {
                state
            }
        }

        is MainUiEvent.DescriptionChanged ->
            state.copy(description = event.value, error = null)

        is MainUiEvent.TypeChanged -> state.copy(type = event.type, error = null)

        MainUiEvent.InvalidTransaction ->
            state.copy(error = TransactionError.INVALID_INPUT)

        MainUiEvent.AmountTooLarge ->
            state.copy(error = TransactionError.AMOUNT_TOO_LARGE)

        is MainUiEvent.AddTransaction -> {
            val transaction = event.transaction
            require(transaction.amount > 0) {
                "Сумма операции должна быть положительной"
            }
            require(transaction.description.isNotBlank()) {
                "Описание операции не должно быть пустым"
            }

            val change = when (transaction.type) {
                TransactionType.INCOME -> transaction.amount.toLong()
                TransactionType.EXPENSE -> -transaction.amount.toLong()
            }
            val newBalance = state.balance.toLong() + change
            val intRange = Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()

            if (newBalance !in intRange) {
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
}

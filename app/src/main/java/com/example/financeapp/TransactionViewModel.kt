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

            amount == null ->
                MainUiEvent.AmountTooLarge

            amount <= 0 ->
                MainUiEvent.InvalidTransaction

            else ->
                MainUiEvent.AddTransaction(
                    Transaction(
                        amount = amount,
                        type = uiState.type,
                        description = description,
                    ),
                )
        }
        onEvent(event)
    }

    private fun reduce(
        state: MainUiState,
        event: MainUiEvent,
    ): MainUiState = when (event) {
        is MainUiEvent.AmountChanged ->
            if (event.value.all(Char::isDigit)) {
                state.copy(amount = event.value, error = null)
            } else {
                state
            }

        is MainUiEvent.DescriptionChanged ->
            state.copy(description = event.value, error = null)

        is MainUiEvent.TypeChanged ->
            state.copy(type = event.type, error = null)

        MainUiEvent.InvalidTransaction ->
            state.copy(error = TransactionError.INVALID_INPUT)

        MainUiEvent.AmountTooLarge ->
            state.copy(error = TransactionError.AMOUNT_TOO_LARGE)

        is MainUiEvent.AddTransaction ->
            applyTransaction(state, event.transaction)
    }

    private fun applyTransaction(
        state: MainUiState,
        transaction: Transaction,
    ): MainUiState {
        require(transaction.amount > 0)
        require(transaction.description.isNotBlank())
        val change = if (transaction.type == TransactionType.INCOME) {
            transaction.amount
        } else {
            -transaction.amount
        }
        val next = state.balance.toLong() + change
        return if (next !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
            state.copy(error = TransactionError.AMOUNT_TOO_LARGE)
        } else {
            state.copy(
                amount = "",
                description = "",
                balance = next.toInt(),
                transactionCount = state.transactionCount + 1,
                transactions = state.transactions + transaction,
                error = null,
            )
        }
    }
}

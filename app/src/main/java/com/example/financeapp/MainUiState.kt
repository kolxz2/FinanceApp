package com.example.financeapp

internal data class MainUiState(
    val balance: Int = 0,
    val count: Int = 0,
)

internal enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
}

internal data class Transaction(
    val amount: Int = 0,
    val transactionType: TransactionType,
)

internal sealed interface MainUiEvent {
    data class AddTransaction(val transaction: Transaction) : MainUiEvent
}

internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val balanceChange = when (event.transaction.transactionType) {
            TransactionType.DEPOSIT -> state.balance + event.transaction.amount
            TransactionType.WITHDRAW -> state.balance - event.transaction.amount
        }

        state.copy(count = state.count + 1, balance = balanceChange)
    }
}

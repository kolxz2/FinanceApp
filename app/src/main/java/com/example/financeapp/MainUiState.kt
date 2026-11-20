package com.example.financeapp

private const val INITIAL_BALANCE = 25_000

internal data class MainUiState(
    val balance: Int = INITIAL_BALANCE,
    val transactionCount: Int = 0,
)

internal enum class TransactionType {
    INCOME,
    EXPENSE,
}

internal data class Transaction(
    val type: TransactionType,
    val amount: Int,
)

internal sealed interface MainUiEvent {
    data class TransactionAdded(val transaction: Transaction) : MainUiEvent
}

internal fun reduce(
    state: MainUiState,
    event: MainUiEvent,
): MainUiState = when (event) {
    is MainUiEvent.TransactionAdded -> {
        val transaction = event.transaction
        require(transaction.amount > 0) { "Transaction amount must be positive" }
        val balanceChange = when (transaction.type) {
            TransactionType.INCOME -> transaction.amount
            TransactionType.EXPENSE -> -transaction.amount
        }
        state.copy(
            balance = state.balance + balanceChange,
            transactionCount = state.transactionCount + 1,
        )
    }
}

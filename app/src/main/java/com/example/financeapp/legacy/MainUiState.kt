package com.example.financeapp.legacy
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
    val amount: Int,
    val type: TransactionType,
)

internal sealed interface MainUiEvent {
    data class TransactionAdded(val transaction: Transaction) : MainUiEvent
}

internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.TransactionAdded -> {
        val transaction = event.transaction
        require(transaction.amount > 0) { "Сумма операции должна быть положительной" }

        val delta = when (transaction.type) {
            TransactionType.INCOME -> transaction.amount
            TransactionType.EXPENSE -> -transaction.amount
        }

        state.copy(
            balance = state.balance + delta,
            transactionCount = state.transactionCount + 1,
        )
    }
}

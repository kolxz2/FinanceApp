package com.example.financeapp.domain

internal data class Transaction(
    val id: Long = 0,
    val amount: Int,
    val type: TransactionType,
    val description: String,
) {
    val signedAmount: Long
        get() = when (type) {
            TransactionType.INCOME -> amount.toLong()
            TransactionType.EXPENSE -> -amount.toLong()
        }
}

internal enum class TransactionType { INCOME, EXPENSE }

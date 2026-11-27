package com.example.financeapp.domain

import kotlinx.coroutines.flow.Flow

internal interface TransactionRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun add(transaction: Transaction)
}

internal class TransactionStorageException(cause: Exception) : Exception(cause)

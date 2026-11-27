package com.example.financeapp.data

import android.database.sqlite.SQLiteException
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.domain.TransactionStorageException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomTransactionRepository(private val dao: TransactionDao) : TransactionRepository {
    override fun observeTransactions(): Flow<List<Transaction>> = dao.observeAll().map { rows ->
        rows.map { row ->
            Transaction(id = row.id, amount = row.amount, type = row.type, description = row.description)
        }
    }

    @Suppress("ThrowsCount")
    override suspend fun add(transaction: Transaction) {
        try {
            dao.insert(
                TransactionEntity(
                    amount = transaction.amount,
                    type = transaction.type,
                    description = transaction.description,
                ),
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: SQLiteException) {
            throw TransactionStorageException(error)
        } catch (error: IllegalStateException) {
            throw TransactionStorageException(error)
        }
    }
}

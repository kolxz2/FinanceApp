package com.example.financeapp

import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

private const val DATABASE_LOG_TAG = "TransactionDatabase"

internal class TransactionRepository(context: Context) {
    private val database = TransactionDatabase.open(context)
    private val dao = database.transactionDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun load(onResult: (List<Transaction>?) -> Unit) {
        executor.execute {
            val transactions = try {
                dao.readAll().map(TransactionEntity::toTransaction)
            } catch (error: SQLiteException) {
                Log.e(DATABASE_LOG_TAG, "Could not load transactions", error)
                null
            } catch (error: IllegalStateException) {
                Log.e(DATABASE_LOG_TAG, "Could not load transactions", error)
                null
            }
            mainHandler.post { onResult(transactions) }
        }
    }

    fun save(transaction: Transaction, onResult: (Boolean) -> Unit) {
        executor.execute {
            val saved = try {
                dao.insert(TransactionEntity.fromTransaction(transaction))
                true
            } catch (error: SQLiteException) {
                Log.e(DATABASE_LOG_TAG, "Could not save transaction", error)
                false
            } catch (error: IllegalStateException) {
                Log.e(DATABASE_LOG_TAG, "Could not save transaction", error)
                false
            }
            mainHandler.post { onResult(saved) }
        }
    }

    fun close() {
        executor.execute(database::close)
        executor.shutdown()
    }
}

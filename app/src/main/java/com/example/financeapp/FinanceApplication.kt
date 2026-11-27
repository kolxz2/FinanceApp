package com.example.financeapp

import android.app.Application
import com.example.financeapp.data.RoomTransactionRepository
import com.example.financeapp.data.TransactionDatabase
import com.example.financeapp.domain.TransactionRepository

class FinanceApplication : Application() {
    internal val transactions: TransactionRepository by lazy {
        RoomTransactionRepository(TransactionDatabase.open(this).transactionDao())
    }
}

package com.example.financeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id ASC")
    fun readAll(): List<TransactionEntity>

    @Insert
    fun insert(transaction: TransactionEntity)
}

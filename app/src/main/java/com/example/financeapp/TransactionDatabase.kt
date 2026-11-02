package com.example.financeapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "finance.db"

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(TransactionTypeConverter::class)
internal abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        fun open(context: Context, name: String = DATABASE_NAME): TransactionDatabase =
            Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, name).build()
    }
}

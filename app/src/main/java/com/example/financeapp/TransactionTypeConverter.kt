package com.example.financeapp

import androidx.room.TypeConverter

internal class TransactionTypeConverter {
    @TypeConverter
    fun fromStoredValue(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun toStoredValue(type: TransactionType): String = type.name
}

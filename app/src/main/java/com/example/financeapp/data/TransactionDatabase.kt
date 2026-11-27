package com.example.financeapp.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.financeapp.domain.TransactionType
import kotlinx.coroutines.flow.Flow

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(TransactionTypeConverter::class)
internal abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        fun open(context: Context): TransactionDatabase =
            Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, "finance.db").build()
    }
}

@Entity(tableName = "transactions")
internal data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val type: TransactionType,
    val description: String,
)

@Dao
internal interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(transaction: TransactionEntity)
}

internal class TransactionTypeConverter {
    @TypeConverter
    fun fromStoredValue(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun toStoredValue(type: TransactionType): String = type.name
}

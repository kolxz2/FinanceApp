package com.example.financeapp

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.financeapp.data.RoomTransactionRepository
import com.example.financeapp.data.TransactionDatabase
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TransactionPersistenceTest {
    @Test
    fun readsLegacyDatabaseAndKeepsNewTransactionsAfterReopening() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "finance-test-${UUID.randomUUID()}.db"
        try {
            createLegacyDatabase(context, name)
            withTimeout(10_000) {
                withRepository(context, name) { repository ->
                    val original = repository.observeTransactions().first().single()
                    assertEquals(Transaction(1, 500, TransactionType.INCOME, "Зарплата"), original)
                    repository.add(Transaction(amount = 125, type = TransactionType.EXPENSE, description = "Проезд"))
                }
                withRepository(context, name) { repository ->
                    val transactions = repository.observeTransactions().first()
                    assertEquals(listOf("Проезд", "Зарплата"), transactions.map(Transaction::description))
                    assertEquals(375L, transactions.sumOf(Transaction::signedAmount))
                }
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    private suspend fun withRepository(
        context: Context,
        name: String,
        block: suspend (RoomTransactionRepository) -> Unit,
    ) {
        val database = Room.databaseBuilder(context, TransactionDatabase::class.java, name).build()
        try {
            block(RoomTransactionRepository(database.transactionDao()))
        } finally {
            database.close()
        }
    }

    private fun createLegacyDatabase(context: Context, name: String) {
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { database ->
            database.execSQL(
                "CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "amount INTEGER NOT NULL, type TEXT NOT NULL, description TEXT NOT NULL)",
            )
            database.execSQL("CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
            database.execSQL("INSERT INTO room_master_table VALUES (42, '97ef1cd04f37ef565efcf9dcc3bc369d')")
            database.execSQL("INSERT INTO transactions (amount, type, description) VALUES (500, 'INCOME', 'Зарплата')")
            database.version = 1
        }
    }
}

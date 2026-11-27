package com.example.financeapp

import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTest {
    @Test
    fun balanceSupportsTotalsAboveIntRange() {
        val income = Transaction(amount = Int.MAX_VALUE, type = TransactionType.INCOME, description = "Доход")
        val expense = Transaction(amount = 1, type = TransactionType.EXPENSE, description = "Расход")

        val balance = listOf(income, income, expense).sumOf(Transaction::signedAmount)

        assertEquals(4_294_967_293L, balance)
    }
}

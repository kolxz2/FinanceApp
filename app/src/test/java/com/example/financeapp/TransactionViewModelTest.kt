package com.example.financeapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class TransactionViewModelTest {
    private val viewModel = TransactionViewModel()

    @Test
    fun initialStateHasNoTransactions() {
        val state = viewModel.uiState

        assertEquals(0, state.balance)
        assertEquals(0, state.transactionCount)
        assertTrue(state.transactions.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun invalidInputShowsErrorAndKeepsBalance() {
        viewModel.addTransaction()

        assertEquals(TransactionError.INVALID_INPUT, viewModel.uiState.error)
        assertEquals(0, viewModel.uiState.balance)
        assertTrue(viewModel.uiState.transactions.isEmpty())
    }

    @Test
    fun amountOutOfIntRangeShowsAmountTooLarge() {
        viewModel.onEvent(MainUiEvent.DescriptionChanged("Кофе"))
        viewModel.onEvent(MainUiEvent.AmountChanged("2147483648"))

        viewModel.addTransaction()

        assertEquals(TransactionError.AMOUNT_TOO_LARGE, viewModel.uiState.error)
        assertEquals(0, viewModel.uiState.balance)
    }

    @Test
    fun zeroAmountShowsInvalidInput() {
        viewModel.onEvent(MainUiEvent.DescriptionChanged("Кофе"))
        viewModel.onEvent(MainUiEvent.AmountChanged("0"))

        viewModel.addTransaction()

        assertEquals(TransactionError.INVALID_INPUT, viewModel.uiState.error)
    }

    @Test
    fun expenseDecreasesBalanceAndClearsForm() {
        viewModel.onEvent(MainUiEvent.DescriptionChanged("  Кофе  "))
        viewModel.onEvent(MainUiEvent.AmountChanged("250"))
        viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE))

        viewModel.addTransaction()

        val state = viewModel.uiState
        assertEquals(-250, state.balance)
        assertEquals(1, state.transactionCount)
        assertEquals(1, state.transactions.size)
        assertEquals("Кофе", state.transactions.single().description)
        assertEquals("", state.amount)
        assertEquals("", state.description)
        assertNull(state.error)
    }

    @Test
    fun incomeIncreasesBalance() {
        viewModel.onEvent(MainUiEvent.DescriptionChanged("Зарплата"))
        viewModel.onEvent(MainUiEvent.AmountChanged("500"))
        viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME))

        viewModel.addTransaction()

        assertEquals(500, viewModel.uiState.balance)
        assertEquals(1, viewModel.uiState.transactionCount)
    }

    @Test
    fun nonDigitAmountIsIgnored() {
        viewModel.onEvent(MainUiEvent.AmountChanged("25a"))

        assertEquals("", viewModel.uiState.amount)
    }

    @Test
    fun balanceOverflowKeepsHistoryUnchanged() {
        viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME))
        viewModel.onEvent(MainUiEvent.DescriptionChanged("Максимум"))
        viewModel.onEvent(MainUiEvent.AmountChanged(Int.MAX_VALUE.toString()))
        viewModel.addTransaction()

        viewModel.onEvent(MainUiEvent.DescriptionChanged("Ещё"))
        viewModel.onEvent(MainUiEvent.AmountChanged("1"))
        viewModel.addTransaction()

        val state = viewModel.uiState
        assertEquals(TransactionError.AMOUNT_TOO_LARGE, state.error)
        assertEquals(Int.MAX_VALUE, state.balance)
        assertEquals(1, state.transactionCount)
        assertEquals(1, state.transactions.size)
    }
}

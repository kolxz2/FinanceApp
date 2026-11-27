package com.example.financeapp.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

internal sealed interface HistoryState {
    data object Loading : HistoryState
    data class Ready(val transactions: List<Transaction>, val balance: Long) : HistoryState
    data object LoadFailed : HistoryState
}

internal enum class HistoryIntent { RETRY }

internal class HistoryViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val state = mutableState.asStateFlow()
    private var observation: Job? = null

    init {
        observe()
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.RETRY -> if (state.value == HistoryState.LoadFailed) observe()
        }
    }

    private fun observe() {
        observation?.cancel()
        mutableState.value = HistoryState.Loading
        observation = viewModelScope.launch {
            repository.observeTransactions()
                .catch { error ->
                    Log.e("FinanceHistory", "Could not read transactions", error)
                    mutableState.value = HistoryState.LoadFailed
                }
                .collect { transactions ->
                    mutableState.value = HistoryState.Ready(transactions, transactions.sumOf(Transaction::signedAmount))
                }
        }
    }
}

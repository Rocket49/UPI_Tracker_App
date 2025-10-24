package com.example.upitracker4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.upitracker4.data.TimeFilter
import com.example.upitracker4.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class HistoryViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _timeFilter = MutableStateFlow(TimeFilter.ALL_TIME)
    val timeFilter = _timeFilter.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val paidTransactions = _timeFilter.flatMapLatest { filter ->
        val startTime = getStartTimeForFilter(filter)
        repository.getPaidTransactions(startTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val paidTransactionsCount = _timeFilter.flatMapLatest { filter ->
        val startTime = getStartTimeForFilter(filter)
        repository.getPaidTransactionsCount(startTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val totalAmountReceived = _timeFilter.flatMapLatest { filter ->
        val startTime = getStartTimeForFilter(filter)
        repository.getTotalAmountReceived(startTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    private fun getStartTimeForFilter(filter: TimeFilter): Long {
        val calendar = Calendar.getInstance()
        return when (filter) {
            TimeFilter.LAST_HOUR -> calendar.apply { add(Calendar.HOUR_OF_DAY, -1) }.timeInMillis
            TimeFilter.LAST_2_HOURS -> calendar.apply { add(Calendar.HOUR_OF_DAY, -2) }.timeInMillis
            TimeFilter.LAST_6_HOURS -> calendar.apply { add(Calendar.HOUR_OF_DAY, -6) }.timeInMillis
            TimeFilter.LAST_12_HOURS -> calendar.apply { add(Calendar.HOUR_OF_DAY, -12) }.timeInMillis
            // CORRECTED: "Today" now means the last 24 hours.
            TimeFilter.TODAY -> calendar.apply { add(Calendar.HOUR_OF_DAY, -24) }.timeInMillis
            TimeFilter.THIS_WEEK -> calendar.apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeek) }.timeInMillis
            TimeFilter.THIS_MONTH -> calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
            TimeFilter.ALL_TIME -> 0L
        }
    }
}

class HistoryViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

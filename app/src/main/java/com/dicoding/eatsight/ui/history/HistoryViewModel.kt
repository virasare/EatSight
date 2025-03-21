package com.dicoding.eatsight.ui.history

import androidx.lifecycle.*
import com.dicoding.eatsight.data.HistoryEntity
import com.dicoding.eatsight.data.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    val allHistory: LiveData<List<HistoryEntity>> = repository.allHistory

    fun insertHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.insertHistory(history)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }

    fun deleteSingleHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteSingleHistory(history)
        }
    }
}
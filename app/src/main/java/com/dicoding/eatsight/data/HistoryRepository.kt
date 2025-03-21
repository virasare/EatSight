package com.dicoding.eatsight.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: LiveData<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun insertHistory(history: HistoryEntity) {
        historyDao.insert(history)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAll()
    }

    suspend fun deleteSingleHistory(history: HistoryEntity) {
        historyDao.deleteSingle(history)
    }
}
package com.dicoding.eatsight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val classificationResult: String,
    val timestamp: Long = System.currentTimeMillis()
)
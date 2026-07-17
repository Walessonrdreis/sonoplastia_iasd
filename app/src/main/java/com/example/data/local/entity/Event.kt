package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val eventDate: Long, // timestamp for the day
    val startTime: String,
    val endTime: String,
    val requiredOperators: Int,
    val isRecurring: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

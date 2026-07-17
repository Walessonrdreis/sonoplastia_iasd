package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "availabilities")
data class Availability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val dayOfWeek: Int, // 1 = Sunday, ..., 7 = Saturday
    val startTime: String, // e.g., "18:00"
    val endTime: String,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

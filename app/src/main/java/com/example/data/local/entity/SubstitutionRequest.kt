package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "substitution_requests")
data class SubstitutionRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scheduleId: Int,
    val requesterId: Int,
    val reason: String,
    val status: String, // PENDING, APPROVED, REJECTED, FULFILLED
    val createdAt: Long = System.currentTimeMillis()
)

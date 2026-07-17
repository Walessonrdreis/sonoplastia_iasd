package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String, // "ADMIN" or "VOLUNTEER"
    val status: String, // "ACTIVE" or "INACTIVE"
    val createdAt: Long = System.currentTimeMillis(),
    val level: String = "Iniciante" // Iniciante, Intermediário, Avançado, Líder
)

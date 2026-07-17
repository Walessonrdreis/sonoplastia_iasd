package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val userId: Int,
    val status: String, // ESCALADO, CONFIRMADO, RECUSADO, CONCLUIDO, FALTOU, SUBSTITUICAO_SOLICITADA
    val createdAt: Long = System.currentTimeMillis()
)

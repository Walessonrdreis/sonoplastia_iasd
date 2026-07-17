package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSchedulesForUser(userId: Int): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE eventId = :eventId")
    fun getSchedulesForEvent(eventId: Int): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @androidx.room.Delete
    suspend fun deleteSchedule(schedule: Schedule)
}

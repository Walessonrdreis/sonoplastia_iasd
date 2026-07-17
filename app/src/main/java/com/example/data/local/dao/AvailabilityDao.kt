package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Availability
import kotlinx.coroutines.flow.Flow

@Dao
interface AvailabilityDao {
    @Query("SELECT * FROM availabilities WHERE userId = :userId")
    fun getAvailabilitiesForUser(userId: Int): Flow<List<Availability>>

    @Query("SELECT * FROM availabilities")
    fun getAllAvailabilities(): Flow<List<Availability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(availability: Availability)

    @Update
    suspend fun updateAvailability(availability: Availability)
}

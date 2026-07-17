package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SubstitutionRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface SubstitutionRequestDao {
    @Query("SELECT * FROM substitution_requests")
    fun getAllRequests(): Flow<List<SubstitutionRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: SubstitutionRequest)

    @Update
    suspend fun updateRequest(request: SubstitutionRequest)
}

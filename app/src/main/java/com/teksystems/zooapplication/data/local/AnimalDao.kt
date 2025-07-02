package com.teksystems.zooapplication.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT MAX(fetchedAt) FROM animals")
    suspend fun getLastFetchTime(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals")
    suspend fun clearAll()
}

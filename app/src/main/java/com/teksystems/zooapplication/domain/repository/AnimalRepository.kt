package com.teksystems.zooapplication.domain.repository

import com.teksystems.zooapplication.domain.model.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAnimals(): Flow<List<Animal>>
    suspend fun refreshAnimalsIfNeeded()
}

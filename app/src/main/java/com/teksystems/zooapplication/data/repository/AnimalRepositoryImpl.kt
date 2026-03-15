package com.teksystems.zooapplication.data.repository

import com.teksystems.zooapplication.data.local.AnimalDao
import com.teksystems.zooapplication.data.local.AnimalEntity
import com.teksystems.zooapplication.data.remote.AnimalApiService
import com.teksystems.zooapplication.data.remote.AnimalItem
import com.teksystems.zooapplication.domain.model.Animal
import com.teksystems.zooapplication.domain.repository.AnimalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(
    private val apiService: AnimalApiService,
    private val animalDao: AnimalDao
) : AnimalRepository {

    override fun getAnimals(): Flow<List<Animal>> =
        animalDao.getAllAnimals().map { entities -> entities.map { it.toDomainModel() } }

    override suspend fun refreshAnimalsIfNeeded() = withContext(Dispatchers.IO) {
        val lastFetch = animalDao.getLastFetchTime()
        val needsRefresh = lastFetch == null || (System.currentTimeMillis() - lastFetch) > CACHE_DURATION_MS
        if (!needsRefresh) return@withContext

        val currentTime = System.currentTimeMillis()
        val allEntities = CATEGORIES.flatMap { category ->
            val response = apiService.getAnimals(name = category)
            response.body()?.take(RESULTS_PER_CATEGORY)?.map { apiAnimal ->
                apiAnimal.toDomainModel(category).toEntity(fetchedAt = currentTime)
            } ?: emptyList()
        }

        animalDao.clearAll()
        animalDao.insertAnimals(allEntities)
    }

    private fun AnimalEntity.toDomainModel(): Animal = Animal(
        name = name,
        commonName = commonName ?: name,
        phylum = phylum,
        scientificName = scientificName,
        category = category,
        slogan = slogan,
        lifespan = lifespan,
        habitat = habitat,
        wingspan = wingspan,
        prey = prey,
        predators = predators
    )

    private fun AnimalItem.toDomainModel(category: String): Animal = Animal(
        name = name,
        commonName = characteristics.commonName ?: name,
        phylum = taxonomy.phylum,
        scientificName = taxonomy.scientificName,
        category = category,
        slogan = characteristics.slogan,
        lifespan = characteristics.lifespan,
        habitat = characteristics.habitat,
        prey = characteristics.prey
    )

    private fun Animal.toEntity(fetchedAt: Long): AnimalEntity = AnimalEntity(
        category = category,
        name = name,
        commonName = commonName,
        phylum = phylum,
        scientificName = scientificName,
        slogan = slogan,
        lifespan = lifespan,
        habitat = habitat,
        wingspan = wingspan,
        prey = prey,
        predators = predators,
        fetchedAt = fetchedAt
    )

    companion object {
        private const val CACHE_DURATION_MS = 10 * 60 * 1000L
        private const val RESULTS_PER_CATEGORY = 3
        val CATEGORIES = listOf("dog", "bird", "bug")
    }
}

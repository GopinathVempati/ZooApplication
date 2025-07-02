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

    override fun getAnimals(): Flow<List<Animal>> {
        return animalDao.getAllAnimals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshAnimalsIfNeeded() = withContext(Dispatchers.IO) {
        val lastFetch = animalDao.getLastFetchTime()
        val now = System.currentTimeMillis()
        val tenMinutesMillis = 10 * 60 * 1000
        val needsRefresh = lastFetch == null || (now - lastFetch) > tenMinutesMillis

        if (!needsRefresh) return@withContext

        try {
            animalDao.clearAll()
            val categories = listOf("dog", "bird", "bug")
            val currentTime = System.currentTimeMillis()
            val allEntities = mutableListOf<AnimalEntity>()

            for (category in categories) {
                val response = apiService.getAnimals(name = category)
                if (response.isSuccessful && response.body() != null) {
                    val limitedResults = response.body()!!.take(3)
                    val entities = limitedResults.map { apiAnimal ->
                        apiAnimal.toDomainModel(category).toEntity(fetchedAt = currentTime)
                    }
                    allEntities.addAll(entities)
                }
            }

            animalDao.insertAnimals(allEntities)
        } catch (e: Exception) {
            throw e
        }
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
        name = this.name,
        commonName = this.characteristics.commonName ?: this.name,
        phylum = this.taxonomy.phylum,
        scientificName = this.taxonomy.scientificName,
        category = category,
        slogan = this.characteristics.slogan,
        lifespan = this.characteristics.lifespan,
        habitat = this.characteristics.habitat,
        prey = this.characteristics.prey
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
}

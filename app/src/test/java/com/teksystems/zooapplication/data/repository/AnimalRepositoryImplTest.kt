package com.teksystems.zooapplication.data.repository

import com.teksystems.zooapplication.data.local.AnimalDao
import com.teksystems.zooapplication.data.local.AnimalEntity
import com.teksystems.zooapplication.data.remote.AnimalApiService
import com.teksystems.zooapplication.data.remote.AnimalItem
import com.teksystems.zooapplication.data.remote.Characteristics
import com.teksystems.zooapplication.data.remote.Taxonomy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AnimalRepositoryImplTest {

    // region Fakes

    private class FakeAnimalDao(
        initialAnimals: List<AnimalEntity> = emptyList(),
        var lastFetchTime: Long? = null
    ) : AnimalDao {
        private val animalsFlow = MutableStateFlow(initialAnimals)
        val insertedAnimals = mutableListOf<AnimalEntity>()
        var clearCalled = false

        override fun getAllAnimals(): Flow<List<AnimalEntity>> = animalsFlow

        override suspend fun getLastFetchTime(): Long? = lastFetchTime

        override suspend fun insertAnimals(animals: List<AnimalEntity>) {
            insertedAnimals += animals
            animalsFlow.value = animals
        }

        override suspend fun clearAll() {
            clearCalled = true
            animalsFlow.value = emptyList()
        }
    }

    private class FakeApiService(
        private val responsesByCategory: Map<String, List<AnimalItem>> = emptyMap()
    ) : AnimalApiService {
        var callCount = 0

        override suspend fun getAnimals(name: String): Response<List<AnimalItem>> {
            callCount++
            return Response.success(responsesByCategory[name] ?: emptyList())
        }
    }

    // endregion

    // region Helpers

    private fun makeAnimalItem(
        name: String,
        commonName: String = name,
        phylum: String = "Chordata",
        scientificName: String = "Sci $name"
    ) = AnimalItem(
        name = name,
        locations = emptyList(),
        characteristics = Characteristics(
            commonName = commonName, lifespan = "10 years", habitat = "Forest",
            prey = "", slogan = "", mostDistinctiveFeature = "", averageLitterSize = "",
            groupBehavior = "", color = "", estimatedPopulationSize = "", biggestThreat = "",
            weight = "", ageOfSexualMaturity = "", skinType = "", lifestyle = "",
            numberOfSpecies = "", ageOfWeaning = "", nameOfYoung = "", gestationPeriod = "",
            topSpeed = "", location = "", diet = "", group = "", height = ""
        ),
        taxonomy = Taxonomy(
            phylum = phylum, genus = "Genus", scientificName = scientificName,
            family = "Family", kingdom = "Animalia", jsonMemberClass = "Class", order = "Order"
        )
    )

    private fun makeEntity(name: String, category: String, fetchedAt: Long = System.currentTimeMillis()) =
        AnimalEntity(
            name = name, commonName = name, phylum = "Chordata", scientificName = "Sci $name",
            category = category, slogan = null, lifespan = "10 years", habitat = "Forest",
            wingspan = null, prey = null, predators = null, fetchedAt = fetchedAt
        )

    // endregion

    // region refreshAnimalsIfNeeded

    @Test
    fun refresh_skipsApiWhenCacheIsFresh() = runTest {
        val dao = FakeAnimalDao(lastFetchTime = System.currentTimeMillis() - 5 * 60 * 1000L)
        val api = FakeApiService()
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded()

        assertEquals(0, api.callCount)
        assertFalse(dao.clearCalled)
    }

    @Test
    fun refresh_fetchesAllCategoriesWhenCacheExpired() = runTest {
        val dao = FakeAnimalDao(lastFetchTime = System.currentTimeMillis() - 15 * 60 * 1000L)
        val api = FakeApiService(
            responsesByCategory = mapOf(
                "dog" to listOf(makeAnimalItem("Labrador")),
                "bird" to listOf(makeAnimalItem("Eagle")),
                "bug" to listOf(makeAnimalItem("Ladybug"))
            )
        )
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded()

        assertEquals(3, api.callCount)
        assertTrue(dao.clearCalled)
        assertEquals(3, dao.insertedAnimals.size)
    }

    @Test
    fun refresh_fetchesWhenNoCacheExists() = runTest {
        val dao = FakeAnimalDao(lastFetchTime = null)
        val api = FakeApiService()
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded()

        assertEquals(3, api.callCount)
        assertTrue(dao.clearCalled)
    }

    @Test
    fun refresh_handlesNullResponseBodyGracefully() = runTest {
        val dao = FakeAnimalDao(lastFetchTime = null)
        val api = object : AnimalApiService {
            override suspend fun getAnimals(name: String): Response<List<AnimalItem>> =
                Response.success(null)
        }
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded() // must not throw

        assertTrue(dao.insertedAnimals.isEmpty())
    }

    @Test
    fun refresh_limitsToThreeResultsPerCategory() = runTest {
        val manyDogs = (1..10).map { makeAnimalItem("Dog$it") }
        val dao = FakeAnimalDao(lastFetchTime = null)
        val api = FakeApiService(responsesByCategory = mapOf("dog" to manyDogs))
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded()

        val dogEntities = dao.insertedAnimals.filter { it.category == "dog" }
        assertEquals(3, dogEntities.size)
    }

    @Test
    fun refresh_insertsEntitiesWithCorrectCategory() = runTest {
        val dao = FakeAnimalDao(lastFetchTime = null)
        val api = FakeApiService(
            responsesByCategory = mapOf(
                "dog" to listOf(makeAnimalItem("Poodle")),
                "bird" to listOf(makeAnimalItem("Parrot")),
                "bug" to listOf(makeAnimalItem("Beetle"))
            )
        )
        val repo = AnimalRepositoryImpl(api, dao)

        repo.refreshAnimalsIfNeeded()

        assertEquals("dog", dao.insertedAnimals.first { it.name == "Poodle" }.category)
        assertEquals("bird", dao.insertedAnimals.first { it.name == "Parrot" }.category)
        assertEquals("bug", dao.insertedAnimals.first { it.name == "Beetle" }.category)
    }

    // endregion

    // region getAnimals

    @Test
    fun getAnimals_mapsEntitiesToDomainModels() = runTest {
        val entity = makeEntity("Eagle", "bird")
        val dao = FakeAnimalDao(initialAnimals = listOf(entity))
        val repo = AnimalRepositoryImpl(FakeApiService(), dao)

        val animals = repo.getAnimals().first()

        assertEquals(1, animals.size)
        with(animals[0]) {
            assertEquals("Eagle", name)
            assertEquals("bird", category)
            assertEquals("Chordata", phylum)
            assertEquals("Sci Eagle", scientificName)
        }
    }

    @Test
    fun getAnimals_fallsBackToNameWhenCommonNameIsNull() = runTest {
        val entity = makeEntity("Eagle", "bird").copy(commonName = null)
        val dao = FakeAnimalDao(initialAnimals = listOf(entity))
        val repo = AnimalRepositoryImpl(FakeApiService(), dao)

        val animals = repo.getAnimals().first()

        assertEquals("Eagle", animals[0].commonName)
    }

    @Test
    fun getAnimals_emitsEmptyListWhenDaoIsEmpty() = runTest {
        val dao = FakeAnimalDao(initialAnimals = emptyList())
        val repo = AnimalRepositoryImpl(FakeApiService(), dao)

        val animals = repo.getAnimals().first()

        assertTrue(animals.isEmpty())
    }

    // endregion
}
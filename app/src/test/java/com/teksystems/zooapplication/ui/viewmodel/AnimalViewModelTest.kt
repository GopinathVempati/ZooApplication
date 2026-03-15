package com.teksystems.zooapplication.ui.viewmodel

import com.teksystems.zooapplication.domain.model.Animal
import com.teksystems.zooapplication.domain.repository.AnimalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimalViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AnimalViewModel

    private val sampleAnimals = listOf(
        Animal("African Wild Dog", "African Wild Dog", "Chordata", "Lycaon pictus", "dog", slogan = "Slogan", lifespan = "12 years"),
        Animal("Eagle", "Eagle", "Chordata", "Aquila chrysaetos", "bird", wingspan = "2m", habitat = "Mountains"),
        Animal("Ladybug", "Ladybug", "Arthropoda", "Coccinellidae", "bug", prey = "Aphids", predators = "Birds")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AnimalViewModel(fakeRepo())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeRepo(
        animals: List<Animal> = sampleAnimals,
        onRefresh: suspend () -> Unit = {}
    ) = object : AnimalRepository {
        override fun getAnimals() = MutableStateFlow(animals)
        override suspend fun refreshAnimalsIfNeeded() = onRefresh()
    }

    @Test
    fun initialLoad_returnsAllAnimals() = runTest {
        advanceUntilIdle()
        assertEquals(3, viewModel.filteredAnimals.value.size)
    }

    @Test
    fun search_filtersByName() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("dog")
        advanceUntilIdle()

        val result = viewModel.filteredAnimals.value
        assertEquals(1, result.size)
        assertEquals("African Wild Dog", result[0].name)
    }

    @Test
    fun search_filtersByCommonName() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Ladybug")
        advanceUntilIdle()

        val result = viewModel.filteredAnimals.value
        assertEquals(1, result.size)
        assertEquals("Ladybug", result[0].commonName)
    }

    @Test
    fun search_isCaseInsensitive() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("EAGLE")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredAnimals.value.size)
        assertEquals("Eagle", viewModel.filteredAnimals.value[0].name)
    }

    @Test
    fun search_noMatchReturnsEmptyList() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("xyz123")
        advanceUntilIdle()

        assertTrue(viewModel.filteredAnimals.value.isEmpty())
    }

    @Test
    fun clearingQuery_restoresFullList() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("eagle")
        advanceUntilIdle()
        assertEquals(1, viewModel.filteredAnimals.value.size)

        viewModel.onSearchQueryChange("")
        advanceUntilIdle()
        assertEquals(3, viewModel.filteredAnimals.value.size)
    }

    @Test
    fun refreshFailure_setsErrorMessage() = runTest {
        val vm = AnimalViewModel(fakeRepo(onRefresh = { throw RuntimeException("Network error") }))
        advanceUntilIdle()

        assertNotNull(vm.errorMessage.value)
        assertTrue(vm.errorMessage.value!!.isNotBlank())
    }

    @Test
    fun initialErrorMessage_isNull() = runTest {
        // Error starts null before any refresh attempt completes
        assertNull(viewModel.errorMessage.value)
    }
}

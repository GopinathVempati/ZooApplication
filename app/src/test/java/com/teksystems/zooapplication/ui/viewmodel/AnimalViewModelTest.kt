
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val sampleAnimals = listOf(
            Animal("African Wild Dog", "African Wild Dog", "Chordata", "Lycaon pictus", "dog", "Slogan", "12 years"),
            Animal("Eagle", "Eagle", "Chordata", "Aquila chrysaetos", "bird", wingspan = "2m", habitat = "Mountains"),
            Animal("Ladybug", "Ladybug", "Arthropoda", "Coccinellidae", "bug", prey = "Aphids", predators = "Birds")
        )

        val fakeRepo = object : AnimalRepository {
            override fun getAnimals() = MutableStateFlow(sampleAnimals)
            override suspend fun refreshAnimalsIfNeeded() = Unit
        }

        viewModel = AnimalViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun filterAnimals_byNameOrCommonName() = runTest {
        // Advance time to trigger init block
        advanceUntilIdle()

        val initialList = viewModel.filteredAnimals.value
        assertEquals(3, initialList.size)

        viewModel.onSearchQueryChange("dog")
        advanceUntilIdle()

        val filteredList = viewModel.filteredAnimals.value
        assertEquals(1, filteredList.size)
        assertTrue(
            filteredList[0].name.contains("dog", ignoreCase = true) ||
                    filteredList[0].commonName.contains("dog", ignoreCase = true)
        )
        assertEquals("African Wild Dog", filteredList[0].name)
    }

}

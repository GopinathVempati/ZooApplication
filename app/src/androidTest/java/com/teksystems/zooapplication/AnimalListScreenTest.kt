package com.teksystems.zooapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.teksystems.zooapplication.domain.model.Animal
import com.teksystems.zooapplication.ui.components.AnimalListScreen
import org.junit.Rule
import org.junit.Test

class AnimalListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleAnimals = listOf(
        Animal(
            name = "African Wild Dog",
            commonName = "African Wild Dog",
            phylum = "Chordata",
            scientificName = "Lycaon pictus",
            category = "dog",
            slogan = "The African painted dog",
            lifespan = "12 years"
        ),
        Animal(name="Eagle", commonName="Eagle", phylum="Chordata", scientificName="Aquila chrysaetos",
               category="bird", wingspan="2.3m", habitat="Mountains"),
        Animal(name="Ladybug", commonName="Ladybug", phylum="Arthropoda", scientificName="Coccinellidae",
               category="bug", prey="Aphids", predators="Birds, frogs")
    )

    @Test
    fun animalList_displaysAllCategories() {
        composeTestRule.setContent {
            AnimalListScreen(
                animals = sampleAnimals,
                searchQuery = "",
                onSearchQueryChange = {  }
            )
        }
        composeTestRule.onNodeWithText("African Wild Dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eagle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ladybug").assertIsDisplayed()
        composeTestRule.onNodeWithText("Slogan: The African painted dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wingspan: 2.3m").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prey: Aphids").assertIsDisplayed()
    }

    @Test
    fun searchFilter_showsOnlyMatchingResults() {
        composeTestRule.setContent {
            var query by remember { mutableStateOf("") }
            val filtered = sampleAnimals.filter { animal ->
                animal.name.contains(query, ignoreCase = true) ||
                        animal.commonName.contains(query, ignoreCase = true)
            }
            AnimalListScreen(
                animals = filtered,
                searchQuery = query,
                onSearchQueryChange = { newQuery -> query = newQuery }
            )
        }

        composeTestRule.onNodeWithText("African Wild Dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eagle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ladybug").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchBox").performTextInput("dog")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("African Wild Dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eagle").assertDoesNotExist()
        composeTestRule.onNodeWithText("Ladybug").assertDoesNotExist()
    }

}

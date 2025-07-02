package com.teksystems.zooapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teksystems.zooapplication.R
import com.teksystems.zooapplication.domain.model.Animal

@Composable
fun AnimalListItem(animal: Animal, modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .padding(8.dp)
        .testTag("AnimalItem")) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = animal.commonName, fontWeight = FontWeight.Bold)
            Text(text = "Phylum: ${animal.phylum}")
            Text(text = "Scientific: ${animal.scientificName}")
            when (animal.category.lowercase()) {
                stringResource(R.string.dog) -> {
                    if (!animal.slogan.isNullOrBlank()) {
                        Text(text = "Slogan: ${animal.slogan}")
                    }
                    if (!animal.lifespan.isNullOrBlank()) {
                        Text(text = "Lifespan: ${animal.lifespan}")
                    }
                }
                stringResource(R.string.bird) -> {
                    if (!animal.wingspan.isNullOrBlank()) {
                        Text(text = "Wingspan: ${animal.wingspan}")
                    }
                    if (!animal.habitat.isNullOrBlank()) {
                        Text(text = "Habitat: ${animal.habitat}")
                    }
                }
                stringResource(R.string.bug) -> {
                    if (!animal.prey.isNullOrBlank()) {
                        Text(text = "Prey: ${animal.prey}")
                    }
                    if (!animal.predators.isNullOrBlank()) {
                        Text(text = "Predators: ${animal.predators}")
                    }
                }
            }
        }
    }
}

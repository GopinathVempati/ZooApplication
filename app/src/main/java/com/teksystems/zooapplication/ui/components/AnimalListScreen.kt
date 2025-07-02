package com.teksystems.zooapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.teksystems.zooapplication.domain.model.Animal

@Composable
fun AnimalListScreen(
    animals: List<Animal>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    errorMessage: String? = null
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("SearchBox"),
            placeholder = { Text("Search animals...") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            singleLine = true
        )
        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
        val orientation = LocalConfiguration.current.orientation
        if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(animals) { animal ->
                    AnimalListItem(animal)
                }
            }
        } else {
            LazyRow(modifier = Modifier.fillMaxSize()) {
                items(animals) { animal ->
                    AnimalListItem(animal, modifier = Modifier.width(250.dp))
                }
            }
        }
    }
}

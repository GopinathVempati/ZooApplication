package com.teksystems.zooapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.teksystems.zooapplication.ui.components.AnimalListScreen
import com.teksystems.zooapplication.ui.theme.ZooApplicationTheme
import com.teksystems.zooapplication.ui.viewmodel.AnimalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimalListActivity : ComponentActivity() {
    private val viewModel: AnimalViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZooApplicationTheme {
                val animals = viewModel.filteredAnimals.collectAsState().value
                val query = viewModel.searchQuery.collectAsState().value
                val errorMsg = viewModel.errorMessage.collectAsState().value
                AnimalListScreen(
                    animals = animals,
                    searchQuery = query,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    errorMessage = errorMsg
                )
            }
        }
    }
}

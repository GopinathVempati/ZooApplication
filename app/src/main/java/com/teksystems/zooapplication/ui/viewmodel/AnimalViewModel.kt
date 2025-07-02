package com.teksystems.zooapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksystems.zooapplication.domain.model.Animal
import com.teksystems.zooapplication.domain.repository.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredAnimals = MutableStateFlow<List<Animal>>(emptyList())
    val filteredAnimals: StateFlow<List<Animal>> = _filteredAnimals.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAnimals()
                .combine(_searchQuery) { allAnimals, query ->
                    if (query.isBlank()) {
                        allAnimals
                    } else {
                        allAnimals.filter { animal ->
                            animal.name.contains(query, ignoreCase = true) || animal.commonName.contains(query, ignoreCase = true)
                        }
                    }
                }
                .collect { filteredList ->
                    _filteredAnimals.value = filteredList
                }
        }
        viewModelScope.launch {
            try {
                repository.refreshAnimalsIfNeeded()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch latest data. Showing cached results."
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

}

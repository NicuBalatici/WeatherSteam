package com.example.weathersteam.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Define a class to hold your screen's state
data class MainUiState(
    val isLoading: Boolean = true,
    val location: String? = null,
    val temperature: String? = null,
    val recommendedGame: String? = null,
    val error: String? = null
)

// 2. Create the ViewModel
class MainViewModel : ViewModel() {

    // Private state that can be changed
    private val _uiState = MutableStateFlow(MainUiState())
    // Public, read-only state for the UI to observe
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Start fetching data as soon as the ViewModel is created
        fetchData()
    }

    // 3. Create a function to fetch the data
    private fun fetchData() {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true) }

            try {
                // --- THIS IS WHERE YOU WOULD DO REAL WORK ---
                // 1. TODO: Get user's location (requires permissions)
                // 2. TODO: Call a Weather API (like OpenWeatherMap)
                // 3. TODO: Run your game recommendation logic

                // Let's FAKE it for now with a 2-second delay
                delay(2000)

                // This is our FAKE data
                val fakeLocation = "Timișoara"
                val fakeTemp = "14°C"
                val fakeGame = "Stardew Valley" // It's a nice day, play something cozy!

                // Update state with success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        location = fakeLocation,
                        temperature = fakeTemp,
                        recommendedGame = fakeGame
                    )
                }
            } catch (e: Exception) {
                // Update state with an error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}"
                    )
                }
            }
        }
    }
}
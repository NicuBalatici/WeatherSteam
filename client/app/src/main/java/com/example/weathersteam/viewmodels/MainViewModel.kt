package com.example.weathersteam.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersteam.data.WeatherData
import com.example.weathersteam.enums.WeatherType
import com.example.weathersteam.handlers.LocationHandler
import com.example.weathersteam.handlers.LocationPermissionException
import com.example.weathersteam.handlers.weatherApiHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val temperature: String? = null,
    val isLoading: Boolean = true,
    val location: String? = null,
    val recommendedGame: String? = null,
    val error: String? = null,
    val needsPermission: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val locationHandler = LocationHandler(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            // Set loading state and clear any previous permission error
            _uiState.update {
                it.copy(isLoading = true, error = null, needsPermission = false)
            }
            try {
                val location = locationHandler.getCurrentLocation()

                if (location != null) {
                    val userLatitude = location.latitude
                    val userLongitude = location.longitude
                    val cityName = locationHandler.getCityName(location)
                    val weatherData = weatherApiHandler(userLatitude, userLongitude)

                    val recommendedGame = "ghim"

                    // 5. Update UI with Success
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            location = cityName,
                            temperature = "${weatherData.temperature}°C",
                            recommendedGame = recommendedGame
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Could not retrieve location.")
                    }
                }
            } catch (e: LocationPermissionException) {
                _uiState.update {
                    it.copy(isLoading = false, needsPermission = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}"
                    )
                }
            }
        }
    }

    fun weatherType(weatherData: WeatherData): WeatherType {
        val moderateRainThreshold = 1.0 // mm
        val heavyRainThreshold = 10.0   // mm
        val windSpeedThreshold = 90.0   // km h
        val snowThreshold = 1.0         // cm
        val cloudsThreshold = 90           // %
        val freezingTemp = 0.5                  // C

        val snowfall = weatherData.snowfall
        val temperature = weatherData.temperature

        if (snowfall >= snowThreshold && temperature <= freezingTemp) {
            return WeatherType.SNOW
        }

        val rain = weatherData.rain
        val windSpeed = weatherData.windSpeed

        if (rain >= heavyRainThreshold && windSpeed >= windSpeedThreshold) {
            return WeatherType.STORM
        }

        if (rain >= moderateRainThreshold) {
            return WeatherType.RAIN
        }

        val clouds = weatherData.cloudCover

        if (clouds >= cloudsThreshold) {
            return WeatherType.CLOUDS
        }

        return WeatherType.SUN
    }
}
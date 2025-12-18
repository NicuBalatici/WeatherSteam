package com.example.weathersteam.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.weathersteam.data.Game
import com.example.weathersteam.data.WeatherData
import com.example.weathersteam.enums.WeatherType
import com.example.weathersteam.handlers.GameHandler
import com.example.weathersteam.handlers.LightSensorHandler
import com.example.weathersteam.handlers.LocationHandler
import com.example.weathersteam.handlers.LocationPermissionException
import com.example.weathersteam.handlers.SoundHandler
import com.example.weathersteam.handlers.weatherApiHandler
import com.example.weathersteam.helpers.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

data class MainUiState(
    val temperature: String? = null,
    val isLoading: Boolean = true,
    val location: String? = null,
    val lighting: String? = null,
    val noise: String? = null,
    val recommendedGame: String? = null,
    val recommendedGameImageUrl: String? = null,
    val error: String? = null,
    val needsPermission: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val locationHandler = LocationHandler(application)
    private val lightSensorHandler = LightSensorHandler(application)
    private val soundHandler = SoundHandler(application)
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalUuidApi::class)
    fun fetchData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, needsPermission = false)
            }
            try {
                val noiseCategory = try {
                    soundHandler.listenAndGetNoiseCategory()
                } catch (e: Exception) {
                    println("Sound Handler failed: ${e.message}")
                    "UNKNOWN"
                }

                val lightingString = try {
                    lightSensorHandler.getLightLevel().first()
                } catch (e: Exception) {
                    "UNKNOWN"
                }

                val location = locationHandler.getCurrentLocation()
                if (location != null) {
                    val userLatitude = location.latitude
                    val userLongitude = location.longitude
                    val cityName = locationHandler.getCityName(location)
                    val weatherData = weatherApiHandler(userLatitude, userLongitude)
                    val weatherType = weatherType(weatherData)

                    val userId = SessionManager(context = application.baseContext).fetchUserIdFromToken()
                    val weatherString = weatherType?.toString() ?: ""

                    val gameHandler = GameHandler()

                    gameHandler.fetchGame(
                        userId = userId,
                        weather = weatherString,
                        mood = lightingString,
                        pace = noiseCategory,
                        difficulty = ""
                    ) { success, game, message ->

                        if (success) {
                            if (game != null) {
                                println("I received the game: ${game.title}")

                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        location = cityName,
                                        temperature = "${weatherData.temperature}°C",
                                        lighting = lightingString,
                                        noise = noiseCategory,
                                        recommendedGame = game.title,
                                        recommendedGameImageUrl = game.imageUrl
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        location = cityName,
                                        temperature = "${weatherData.temperature}°C",
                                        lighting = lightingString,
                                        noise = noiseCategory,
                                        recommendedGame = "No games found",
                                        error = "Try adding more games to your library!"
                                    )
                                }
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, error = "Server Error: $message")
                            }
                        }
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

    fun weatherType(weatherData: WeatherData): WeatherType? {
        val moderateRainThreshold = 1.0
        val heavyRainThreshold = 10.0
        val windSpeedThreshold = 90.0
        val snowThreshold = 1.0
        val cloudsThreshold = 90
        val freezingTemp = 0.5

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

        if (weatherData.isDay)
            return WeatherType.SUN

        return null
    }
}
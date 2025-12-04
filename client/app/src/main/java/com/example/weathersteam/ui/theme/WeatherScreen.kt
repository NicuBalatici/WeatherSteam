package com.example.weathersteam.ui.theme

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathersteam.viewmodels.MainViewModel

@Composable
fun WeatherScreen(
    mainViewModel: MainViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by mainViewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) mainViewModel.fetchData()
        }
    )

    LaunchedEffect(Unit) {
        mainViewModel.fetchData()
    }

    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.needsPermission -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("We need location for weather data.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }) {
                        Text("Grant Permission")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onBackClick) { Text("Go Back") }
                }
            }
            uiState.error != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBackClick) { Text("Go Back") }
                }
            }
            else -> {
                WeatherSuccessView(
                    location = uiState.location,
                    temperature = uiState.temperature,
                    game = uiState.recommendedGame,
                    onRefreshClick = { mainViewModel.fetchData() },
                    onBackClick = onBackClick
                )
            }
        }
    }
}

@Composable
fun WeatherSuccessView(
    location: String?,
    temperature: String?,
    game: String?,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("From where you are (${location ?: "Unknown"}),\nyou have the temperature ${temperature ?: "N/A"}.",
            textAlign = TextAlign.Center, color = Color(0xFF333333))

        Spacer(modifier = Modifier.height(16.dp))

        Text("The recommended game is:", color = Color(0xFF555555))
        Text(game ?: "No recommendation", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRefreshClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Refresh Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        OutlinedButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Back to Menu")
        }
    }
}
package com.example.weathersteam.ui.theme

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SteamBgTop, SteamBgBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight(),
            color = Color(0xFF1A222E),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, SteamBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color = SteamBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing Weather Data...", color = SteamTextSec)
                    }

                    uiState.needsPermission -> {
                        Text(
                            "Location Required",
                            color = SteamTextMain,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "We need your location to recommend games based on the local weather.",
                            color = SteamTextSec,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SteamActionButton(
                            text = "GRANT PERMISSION",
                            onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SteamActionButton(
                            text = "GO BACK",
                            onClick = onBackClick,
                            isPrimary = false
                        )
                    }

                    uiState.error != null -> {
                        Text(
                            "Connection Error",
                            color = Color(0xFFCD544B),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Error: ${uiState.error}",
                            color = SteamTextSec,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SteamActionButton(
                            text = "GO BACK",
                            onClick = onBackClick,
                            isPrimary = false
                        )
                    }

                    else -> {
                        WeatherSuccessView(
                            location = uiState.location,
                            temperature = uiState.temperature,
                            game = uiState.recommendedGame,
                            image = uiState.recommendedGameImageUrl,
                            onRefreshClick = { mainViewModel.fetchData() },
                            onBackClick = onBackClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherSuccessView(
    location: String?,
    temperature: String?,
    game: String?,
    image: String?,
    onRefreshClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CURRENT LOCATION",
            fontSize = 12.sp,
            color = SteamBlue,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${location ?: "Unknown"} • ${temperature ?: "N/A"}",
            fontSize = 22.sp,
            color = SteamTextMain,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "RECOMMENDED FOR YOU",
            fontSize = 12.sp,
            color = SteamTextSec,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f) // Standard game banner ratio
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
                .border(1.dp, SteamBorder, RoundedCornerShape(4.dp))
        ) {
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = "Game Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Image Available", color = SteamTextSec)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = game ?: "No recommendation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SteamTextMain,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        SteamActionButton(
            text = "REFRESH",
            onClick = onRefreshClick,
            isPrimary = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        SteamActionButton(
            text = "BACK TO MENU",
            onClick = onBackClick,
            isPrimary = false
        )
    }
}

@Composable
fun SteamActionButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        border = if (!isPrimary) BorderStroke(1.dp, SteamBorder) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) Color.White else SteamTextSec,
                letterSpacing = 1.sp
            )
        }
    }
}
package com.example.weathersteam.ui.theme

// --- NEW --- Import necessary ActivityResult and Permission tools
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathersteam.viewmodels.MainViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val uiState by mainViewModel.uiState.collectAsState()

    // --- NEW --- Create a launcher for the permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission was granted, retry fetching data
                mainViewModel.fetchData()
            } else {
                // User denied permission, you could show a message here
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            // --- NEW --- Show a permission request button
            uiState.needsPermission -> {
                PermissionView(
                    onGrantPermissionClick = {
                        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                )
            }

            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            // Success State (no changes needed here)
            else -> {
                SuccessView(
                    location = uiState.location,
                    temperature = uiState.temperature,
                    game = uiState.recommendedGame,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

// --- NEW --- A composable for the success state (to clean up the `when` block)
@Composable
fun SuccessView(
    location: String?,
    temperature: String?,
    game: String?,
    onLogoutClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "From where you are (${location ?: "Unknown"}),\nyou have the temperature ${temperature ?: "N/A"}.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The recommended game is:",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF555555)
        )
        Text(
            text = game ?: "No recommendation",
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Logout")
        }
    }
}

// --- NEW --- A composable for the permission request state
@Composable
fun PermissionView(
    onGrantPermissionClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "We need your location to get the weather.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGrantPermissionClick) {
            Text("Grant Permission")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
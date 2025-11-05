package com.example.weathersteam.ui.theme

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
    // Get the ViewModel instance from the Compose lifecycle
    mainViewModel: MainViewModel = viewModel(),
    // This comes from your AppNavigation
    onLogoutClick: () -> Unit = {}
) {
    // Observe the uiState from the ViewModel
    // 'by' keyword makes it automatically unwrap the value
    val uiState by mainViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Use a 'when' block to show UI for each state
        when {
            // 1. Loading State
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            // 2. Error State
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            // 3. Success State
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "From where you are (${uiState.location ?: "Unknown"}),\nyou have the temperature ${uiState.temperature ?: "N/A"}.",
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
                        // Use ?: as a fallback in case data is null
                        text = uiState.recommendedGame ?: "No recommendation",
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
        }
    }
}


// You can create a simple preview for the success state
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // We can't use a real ViewModel in a Preview,
    // so this preview won't show the dynamic data.
    // For a more advanced preview, you would pass a fake ViewModel.
    MainScreen()
}
package com.example.weathersteam.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RandomGameScreen(onBackClick: () -> Unit) {
    var randomGame by remember { mutableStateOf<String?>(null) }
    val dummyGames = listOf("Cyberpunk 2077", "Stardew Valley", "Hades", "Elden Ring", "Minecraft")

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Feeling Lucky?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = randomGame ?: "Press button to pick!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (randomGame != null) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { randomGame = dummyGames.random() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Pick Random Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        OutlinedButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Back to Menu")
        }
    }
}
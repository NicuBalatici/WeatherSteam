package com.example.weathersteam.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyGamesScreen(onBackClick: () -> Unit) {
    val myGames = listOf("Dota 2", "Counter-Strike 2", "Baldur's Gate 3", "Terraria", "Rust")

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        Text("My Library", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF9A825))

        Spacer(modifier = Modifier.height(16.dp))

        // List takes up available space
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(myGames) { game ->
                GameItem(name = game)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button pinned to bottom
        OutlinedButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Back to Menu")
        }
    }
}

@Composable
fun GameItem(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF59D), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(name, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
    }
}
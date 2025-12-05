package com.example.weathersteam.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersteam.data.Game
import com.example.weathersteam.handlers.GameHandler
import com.example.weathersteam.helpers.SessionManager
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun MyGamesScreen(onBackClick: () -> Unit) {

    val context = LocalContext.current
    val userId = SessionManager(context).fetchUserIdFromToken()

    var gameList by remember { mutableStateOf<List<Game>>(emptyList()) }

    LaunchedEffect(Unit) {
        GameHandler().fetchAllUserGames(userId = userId) { success, games, message ->
            if (success && games != null) {
                gameList = games
            }
        }
    }

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
            items(gameList) { game ->
                GameItem(name = game.title, image = game.imageUrl)
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
fun GameItem(name: String, image: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF59D))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        AsyncImage(
            model = image,
            contentDescription = "Game Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(2.65f / 1f)
        )
    }
}
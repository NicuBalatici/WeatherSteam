package com.example.weathersteam.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathersteam.data.Game
import com.example.weathersteam.handlers.GameHandler
import com.example.weathersteam.helpers.SessionManager

@OptIn(ExperimentalTextApi::class)
@Composable
fun MyGamesScreen(onBackClick: () -> Unit) {

    val context = LocalContext.current
    val userId = SessionManager(context).fetchUserIdFromToken()

    var gameList by remember { mutableStateOf<List<Game>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        GameHandler().fetchAllUserGames(userId = userId) { success, games, message ->
            isLoading = false
            if (success && games != null) {
                gameList = games
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SteamBgTop, SteamBgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "MY LIBRARY",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(SteamBlue, Color.White)
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SteamBlue)
                }
            } else if (gameList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No games found in your library.", color = SteamTextSec)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(gameList) { game ->
                        SteamGameItem(name = game.title, image = game.imageUrl)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SteamActionButton(
                text = "BACK TO MENU",
                onClick = onBackClick,
                isPrimary = false
            )
        }
    }
}

@Composable
fun SteamGameItem(name: String, image: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Fixed height for cleaner list
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1A222E)) // Dark Card Background
            .border(1.dp, SteamBorder, RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = image,
            contentDescription = "Game Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SteamTextMain, // White text
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}
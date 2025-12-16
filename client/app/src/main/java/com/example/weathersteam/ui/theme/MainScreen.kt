package com.example.weathersteam.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SteamBgTop = Color(0xFF1B2838)
val SteamBgBottom = Color(0xFF171A21)
val SteamRed = Color(0xFFCD544B) // For Logout

@OptIn(ExperimentalTextApi::class)
@Composable
fun MainScreen(
    username: String,
    onWeatherChoiceClick: () -> Unit,
    onRandomChoiceClick: () -> Unit,
    onMyGamesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Text(
                text = "Welcome back,",
                color = SteamTextSec,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = username.uppercase(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(SteamBlue, Color.White),
                        tileMode = TileMode.Mirror
                    )
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            SteamMenuButton(
                text = "WEATHER CHOICE",
                onClick = onWeatherChoiceClick,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            SteamMenuButton(
                text = "RANDOM CHOICE",
                onClick = onRandomChoiceClick,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            SteamMenuButton(
                text = "MY GAMES",
                onClick = onMyGamesClick,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            SteamMenuButton(
                text = "LOGOUT",
                onClick = onLogoutClick,
                isPrimary = false,
                customColor = SteamRed
            )
        }
    }
}

@Composable
fun SteamMenuButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = true,
    customColor: Color = SteamBlue
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        ),
        border = if (!isPrimary) BorderStroke(1.dp, customColor) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) Color.White else customColor,
                letterSpacing = 2.sp
            )
        }
    }
}
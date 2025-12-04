package com.example.weathersteam.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors
val ButtonBlueBg = Color(0xFF90CAF9)
val ButtonBlueBorder = Color(0xFF42A5F5)
val ButtonGreenBg = Color(0xFFA5D6A7)
val ButtonGreenBorder = Color(0xFF66BB6A)
val ButtonYellowBg = Color(0xFFFFF59D)
val ButtonYellowBorder = Color(0xFFFFEE58)
val ButtonRedBg = Color(0xFFEF9A9A) // Light Red for Logout
val ButtonRedBorder = Color(0xFFEF5350)
val SketchTextColor = Color(0xFF212121)

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
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        SuccessView(
            username = username,
            onWeatherChoiceClick = onWeatherChoiceClick,
            onRandomChoiceClick = onRandomChoiceClick,
            onMyGamesClick = onMyGamesClick,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
fun SuccessView(
    username: String,
    onWeatherChoiceClick: () -> Unit,
    onRandomChoiceClick: () -> Unit,
    onMyGamesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Welcome,\n$username",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = SketchTextColor,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MenuButton(
                text = "Weather Choice",
                backgroundColor = ButtonBlueBg,
                borderColor = ButtonBlueBorder,
                onClick = onWeatherChoiceClick
            )

            MenuButton(
                text = "Random Choice",
                backgroundColor = ButtonGreenBg,
                borderColor = ButtonGreenBorder,
                onClick = onRandomChoiceClick
            )

            MenuButton(
                text = "My Games",
                backgroundColor = ButtonYellowBg,
                borderColor = ButtonYellowBorder,
                onClick = onMyGamesClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            MenuButton(
                text = "Logout",
                backgroundColor = ButtonRedBg,
                borderColor = ButtonRedBorder,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(60.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(3.dp, borderColor),
        shadowElevation = 6.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SketchTextColor
            )
        }
    }
}
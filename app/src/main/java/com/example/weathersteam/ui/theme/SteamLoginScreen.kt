package com.example.weathersteam.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SteamLoginScreen(
    onSteamLoginClick: (String) -> Unit = { _ -> },
    onRegisterClick: () -> Unit = {}
) {
    var steamId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            Text(
                text = "Sign in with Steam ID",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Disclaimer: As automatically authenticating with Steam via OpenID is very tricky, you will have to manually provide your Steam ID or custom URL, in order to sign in.",
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "How to get your Steam ID:",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "1. Sign in on Steam.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF333333),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "2. Go to your profile.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF333333),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "3. Check your profile URL. Your ID is after the last 'slash' (/).",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF333333),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Example:",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, MaterialTheme.shapes.small)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "URL:",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start,
                            color = Color(0xFF333333),
                            modifier = Modifier.width(40.dp)
                        )
                        Text(
                            text = "https://steamcommunity.com/profiles/76561197960435530",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = "Steam ID: 76561197960435530",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Finally, submit your Steam ID below:",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = steamId,
                onValueChange = { steamId = it },
                label = { Text("Steam ID") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSteamLoginClick(steamId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Don't have a Steam Account? Sign in",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SteamLoginScreenPreview() {
    SteamLoginScreen()
}
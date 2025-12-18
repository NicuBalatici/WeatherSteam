package com.example.weathersteam.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersteam.handlers.SteamLoginHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SteamLoginScreen(
    onSteamLogin: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var steamId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

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
                .clip(RoundedCornerShape(4.dp))
                .heightIn(max = 700.dp),
            color = Color(0xFF1A222E),
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, SteamBorder)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "STEAM ID LOGIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SteamBlue, Color.White),
                            tileMode = TileMode.Mirror
                        )
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Disclaimer",
                    color = Color(0xFFCD544B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Authenticating via OpenID is limited. Please manually provide your Steam ID to sign in.",
                    fontSize = 14.sp,
                    color = SteamTextSec,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "How to find your Steam ID:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SteamTextMain,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InstructionStep(1, "Sign in on Steam.")
                    InstructionStep(2, "Go to your profile.")
                    InstructionStep(3, "Check your URL. Your ID is the number at the end.")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000000).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .border(1.dp, SteamBorder, RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "EXAMPLE URL:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SteamBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = SteamTextSec)) {
                                    append(".../profiles/")
                                }
                                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                    append("76561197960435530")
                                }
                            },
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = steamId,
                    onValueChange = { steamId = it },
                    label = { Text("Paste Steam ID Here", color = SteamTextSec) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF232D3D),
                        unfocusedContainerColor = Color(0xFF232D3D).copy(alpha = 0.7f),
                        focusedTextColor = SteamTextMain,
                        unfocusedTextColor = SteamTextMain,
                        focusedBorderColor = SteamBlue,
                        unfocusedBorderColor = SteamBorder,
                        cursorColor = SteamBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { if (steamId.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            try {
                                SteamLoginHandler.performLogin(context, steamId) { success, message ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT)
                                            .show()
                                        onSteamLogin()
                                    } else {
                                        Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(SteamBlue, SteamBlueDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOGIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Don't have an account? Sign up",
                        color = SteamTextMain,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionStep(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "$number.",
            color = SteamBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = text,
            color = SteamTextSec,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SteamLoginScreenPreview() {
    SteamLoginScreen()
}
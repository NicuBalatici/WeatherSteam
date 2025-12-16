package com.example.weathersteam.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersteam.handlers.RegisterHandler

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val registerHandler = remember { RegisterHandler() }
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .clip(RoundedCornerShape(4.dp)), // Sharp corners
            color = Color(0xFF1A222E), // Steam Card Background
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, SteamBorder)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 28.sp,
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

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = SteamTextSec) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF232D3D), // Steam Input Dark
                        unfocusedContainerColor = Color(0xFF232D3D).copy(alpha = 0.7f),
                        focusedTextColor = SteamTextMain,
                        unfocusedTextColor = SteamTextMain,
                        focusedBorderColor = SteamBlue,
                        unfocusedBorderColor = SteamBorder,
                        cursorColor = SteamBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = SteamTextSec) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = SteamTextSec) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true

                            registerHandler.performRegistration(username, email, password) { success, message ->
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    onRegisterSuccess()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(),
                    enabled = !isLoading
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
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "SIGN UP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onLoginClick) {
                    Text(
                        "Already have an account? Log in",
                        color = SteamTextMain, // White text
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUpScreen()
}
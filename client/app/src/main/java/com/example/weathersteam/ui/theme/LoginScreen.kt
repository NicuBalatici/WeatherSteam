package com.example.weathersteam.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersteam.R
import com.example.weathersteam.handlers.LoginHandler

// Define colors if they aren't already globally defined
val SteamDarkBgTop = Color(0xFF1B2838)
val SteamDarkBgBottom = Color(0xFF171A21)
val SteamCardBg = Color(0xFF1A222E)
val SteamInputBg = Color(0xFF232D3D)
val SteamBlue = Color(0xFF66C0F4)
val SteamBlueDark = Color(0xFF4B96C4)
val SteamTextMain = Color(0xFFF0F0F0)
val SteamTextSec = Color(0xFF9099A1)
val SteamBorder = Color(0xFF3D4450)

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSteamLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val loginHandler = remember { LoginHandler() }
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SteamDarkBgTop, SteamDarkBgBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = SteamCardBg,
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, SteamBorder)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "STEAM LOGIN",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SteamBlue, Color.White),
                            tileMode = TileMode.Mirror
                        )
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = SteamTextSec) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SteamInputBg,
                        unfocusedContainerColor = SteamInputBg.copy(alpha = 0.7f),
                        focusedTextColor = SteamTextMain,
                        unfocusedTextColor = SteamTextMain,
                        focusedBorderColor = SteamBlue,
                        unfocusedBorderColor = SteamBorder,
                        cursorColor = SteamBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = SteamTextSec) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SteamInputBg,
                        unfocusedContainerColor = SteamInputBg.copy(alpha = 0.7f),
                        focusedTextColor = SteamTextMain,
                        unfocusedTextColor = SteamTextMain,
                        focusedBorderColor = SteamBlue,
                        unfocusedBorderColor = SteamBorder,
                        cursorColor = SteamBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            loginHandler.performLogin(context, email, password) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    enabled = !isLoading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(colors = listOf(SteamBlue, SteamBlueDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(26.dp), color = Color.White, strokeWidth = 3.dp)
                        } else {
                            Text(
                                text = "SIGN IN",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SteamBorder)
                    Text(
                        text = " OR ",
                        color = SteamTextSec,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SteamBorder)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- BIGGER STEAM BUTTON ---
                OutlinedButton(
                    onClick = onSteamLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp), // Increased height to allow image to grow
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SteamBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SteamInputBg.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sits_01),
                            contentDescription = "Steam Login",
                            // This ensures the image fills the button space
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp) // Minimal padding just so it doesn't touch the edges
                        )
                    }
                }
                // ---------------------------

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onRegisterClick) {
                    Text(
                        text = "Don’t have an account? Sign up free",
                        color = SteamTextMain,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
package com.example.weathersteam.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weathersteam.handlers.GameHandler
import com.example.weathersteam.helpers.SessionManager
import kotlin.math.sqrt

@Composable
fun RandomGameScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    var randomGame by remember { mutableStateOf<String?>(null) }
    var image by remember { mutableStateOf<String?>(null) }
    var lastShakeTime by remember { mutableLongStateOf(0L) }

    fun fetchRandomGame() {
        val userId = SessionManager(context = context).fetchUserIdFromToken()
        val gameHandler = GameHandler()

        gameHandler.fetchGame(
            userId = userId,
            weather = "", // Empty params = pure random
            mood = "",
            pace = "",
            difficulty = ""
        ) { success, game, message ->
            if (success && game != null) {
                randomGame = game.title
                image = game.imageUrl
            }
        }
    }

    // --- SENSOR LOGIC (Keep existing logic) ---
    DisposableEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    val gX = x / SensorManager.GRAVITY_EARTH
                    val gY = y / SensorManager.GRAVITY_EARTH
                    val gZ = z / SensorManager.GRAVITY_EARTH

                    val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

                    if (gForce > 2.5f) { // Shake threshold
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTime > 1000) { // Increased delay slightly to prevent double-fetches
                            lastShakeTime = currentTime
                            fetchRandomGame()
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // --- UI START ---
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
                .padding(24.dp),
            color = Color(0xFF1A222E), // Steam Card Dark
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, SteamBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Header
                Text(
                    text = "SHAKE TO PICK",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF66C0F4), // Steam Blue
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Shake your phone to discover a random game from our library.",
                    fontSize = 14.sp,
                    color = SteamTextSec,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 2. Image Area (The "Monitor")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .border(1.dp, SteamBorder, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (image != null) {
                        AsyncImage(
                            model = image,
                            contentDescription = "Game Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // "No Signal" / Waiting State
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = SteamTextSec,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Waiting for shake...", color = SteamTextSec, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Game Title
                Text(
                    text = randomGame ?: "???",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (randomGame != null) SteamTextMain else SteamTextSec,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 4. Manual Button (Because shaking is hard on Emulators)
                SteamActionButton(
                    text = "ROLL RANDOM GAME",
                    onClick = { fetchRandomGame() },
                    isPrimary = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Back Button
                SteamActionButton(
                    text = "BACK TO MENU",
                    onClick = onBackClick,
                    isPrimary = false
                )
            }
        }
    }
}
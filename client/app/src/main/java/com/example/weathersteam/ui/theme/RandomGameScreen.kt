package com.example.weathersteam.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

@Composable
fun RandomGameScreen(onBackClick: () -> Unit) {
    // 1. Get Context and SensorManager
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    // 2. State for the game and shake logic
    var randomGame by remember { mutableStateOf<String?>(null) }
    var lastShakeTime by remember { mutableLongStateOf(0L) }

    val dummyGames = listOf("Cyberpunk 2077", "Stardew Valley", "Hades", "Elden Ring", "Minecraft", "The Witcher 3", "Celeste")

    // 3. Define the Shake Detection Logic
    DisposableEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    // Calculate G-Force (Acceleration magnitude)
                    val gX = x / SensorManager.GRAVITY_EARTH
                    val gY = y / SensorManager.GRAVITY_EARTH
                    val gZ = z / SensorManager.GRAVITY_EARTH

                    // Formula: sqrt(x² + y² + z²)
                    val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

                    // Threshold: 2.5f is a moderate shake.
                    if (gForce > 2.5f) {
                        val currentTime = System.currentTimeMillis()
                        // Debounce: Only allow a shake every 500ms
                        if (currentTime - lastShakeTime > 500) {
                            lastShakeTime = currentTime
                            randomGame = dummyGames.random()
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Register listener
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        // Cleanup: Unregister when leaving the screen
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Shake to Pick!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "(Or press the button)",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = randomGame ?: "Shake your phone...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (randomGame != null) MaterialTheme.colorScheme.primary else Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Manual Button
        Button(
            onClick = { randomGame = dummyGames.random() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Pick Random Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Back to Menu")
        }
    }
}
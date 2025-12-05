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

                    if (gForce > 2.5f) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTime > 500) {
                            lastShakeTime = currentTime

                            val userId = SessionManager(context = context).fetchUserIdFromToken()
                            val gameHandler = GameHandler()

                            gameHandler.fetchGame(
                                userId = userId,
                                weather = "",
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

        AsyncImage(
            model = image ?: "",
            contentDescription = "Translated description of what the image contains",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().aspectRatio(2.65f / 1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = randomGame ?: "Shake your phone...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (randomGame != null) MaterialTheme.colorScheme.primary else Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Spacer(modifier = Modifier.height(16.dp))

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
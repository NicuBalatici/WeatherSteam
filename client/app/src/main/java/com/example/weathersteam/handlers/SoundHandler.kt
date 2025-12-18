package com.example.weathersteam.handlers

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.log10

class SoundHandler(private val context: Context) {

    private var recorder: MediaRecorder? = null

    suspend fun listenAndGetNoiseCategory(): String {
        val outputFile = File(context.cacheDir, "temp_audio.3gp")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        return try {
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            val decibelReadings = mutableListOf<Double>()
            val durationMillis = 5000L
            val sampleRateMillis = 200L
            var elapsedTime = 0L

            delay(200)

            while (elapsedTime < durationMillis) {
                delay(sampleRateMillis)
                elapsedTime += sampleRateMillis

                val amplitude = recorder?.maxAmplitude ?: 0

                if (amplitude > 0) {
                    val db = 20 * log10(amplitude.toDouble())
                    decibelReadings.add(db)
                    Log.d("SoundHandler", "Sample: $db dB")
                }
            }

            stopRecorder()

            if (decibelReadings.isEmpty()) return "UNKNOWN"

            val averageDb = decibelReadings.average()
            Log.d("SoundHandler", "Average Noise: $averageDb dB")

            return when {
                averageDb < 50.0 -> "QUIET"
                averageDb < 75.0 -> "NORMAL"
                else -> "LOUD"
            }

        } catch (e: Exception) {
            Log.e("SoundHandler", "Error recording", e)
            stopRecorder()
            "UNKNOWN"
        }
    }

    private fun stopRecorder() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) { }
        recorder = null
    }
}
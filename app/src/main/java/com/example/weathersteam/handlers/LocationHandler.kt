package com.example.weathersteam.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHandler(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFinePermission && !hasCoarsePermission) {
            throw LocationPermissionException("Location permission not granted.")
        }

        val priority = if (hasFinePermission) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val cancellationTokenSource = CancellationTokenSource()

        try {
            Log.d(
                "LocationHandler",
                "Attempting fusedLocationClient.getCurrentLocation()"
            ) // <-- ADDED
            val currentLocation = fusedLocationClient.getCurrentLocation(
                priority,
                cancellationTokenSource.token
            ).await()

            if (currentLocation != null) {
                Log.d(
                    "LocationHandler",
                    "getCurrentLocation SUCCESS: $currentLocation"
                ) // <-- ADDED
                return currentLocation
            } else {
                Log.w("LocationHandler", "getCurrentLocation returned NULL") // <-- ADDED
            }

        } catch (e: Exception) {
            // Log this error or handle it
            Log.e("LocationHandler", "getCurrentLocation FAILED", e) // <-- MOST IMPORTANT
        }

        // --- Fallback ---
        try {
            Log.d("LocationHandler", "Attempting fusedLocationClient.lastLocation()") // <-- ADDED
            val last = fusedLocationClient.lastLocation.await()
            if (last != null) {
                Log.d("LocationHandler", "lastLocation SUCCESS: $last") // <-- ADDED
            } else {
                Log.w("LocationHandler", "lastLocation returned NULL") // <-- ADDED
            }
            return last
        } catch (e: Exception) {
            Log.e("LocationHandler", "lastLocation FAILED", e) // <-- ADDED
            return null // Return null if fallback also fails
        }
    }
    fun getCityName(location: Location): String {
        return try {
            val addresses = if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }
            addresses?.firstOrNull()?.locality ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}


class LocationPermissionException(message: String) : Exception(message)

private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T? {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) {
                continuation.resume(result)
            }
        }.addOnFailureListener { exception ->
            if (continuation.isActive) {
                continuation.resumeWithException(exception)
            }
        }.addOnCanceledListener {
            continuation.cancel()
        }
    }
}
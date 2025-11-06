package com.example.weathersteam.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
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
        // 1. Check if permission is granted
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            throw LocationPermissionException("Location permission not granted.")
        }

        // 2. We have permission, try to get the last known location (fast)
        val lastLocation = fusedLocationClient.lastLocation.await()
        if (lastLocation != null) {
            return lastLocation
        }

        // 3. Last location was null, request a fresh one (slower)
        val cancellationTokenSource = CancellationTokenSource()
        return fusedLocationClient.getCurrentLocation(
            // --- CHANGED ---
            // Use this for a faster, block-level accuracy fix.
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            // --- END CHANGE ---
            cancellationTokenSource.token
        ).await()
    }

    fun getCityName(location: Location): String {
        return try {
            val addresses = if (Build.VERSION.SDK_INT >= 33) {
                // Android 13+ (SDK 33) has a new, non-deprecated method
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            } else {
                // Older Android versions
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }
            // 'locality' is the standard field for 'city'
            addresses?.firstOrNull()?.locality ?: "Unknown"
        } catch (e: Exception) {
            "Unknown" // Handle case where geocoder fails
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
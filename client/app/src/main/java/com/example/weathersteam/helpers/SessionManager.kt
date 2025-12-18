package com.example.weathersteam.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONObject
import android.util.Base64

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val WEATHER_STEAM_USER_TOKEN = "user_auth_token"
    }

    fun fetchSteamIdFromToken(): String {
        val token = fetchAuthToken() ?: return ""

        try {
            val parts = token.split(".")
            if (parts.size != 3) return ""
            val payload = parts[1]

            // Decode the payload
            val payloadBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadString = String(payloadBytes)

            val jsonObject = JSONObject(payloadString)

            return if (jsonObject.has("steam_id")) {
                jsonObject.getString("steam_id")
            } else {
                ""
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun fetchUserIdFromToken(): String {
        val token = fetchAuthToken() ?: return "Guest"

        try {
            val parts = token.split(".")
            if (parts.size != 3) return "Guest"
            val payload = parts[1]

            // Decode the payload
            val payloadBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadString = String(payloadBytes)

            val jsonObject = JSONObject(payloadString)

            return if (jsonObject.has("user_id")) {
                jsonObject.getString("user_id")
            } else {
                ""
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return "Guest"
        }
    }

    fun fetchUserFromToken(): String {
        val token = fetchAuthToken() ?: return "Guest"

        try {
            val parts = token.split(".")
            if (parts.size != 3) return "Guest"
            val payload = parts[1]

            // Decode the payload
            val payloadBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadString = String(payloadBytes)

            val jsonObject = JSONObject(payloadString)

            return if (jsonObject.has("sub")) {
                jsonObject.getString("sub")
            } else if (jsonObject.has("username")) {
                jsonObject.getString("username")
            } else if (jsonObject.has("name")) {
                jsonObject.getString("name")
            } else {
                "User"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Guest"
        }
    }

    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(WEATHER_STEAM_USER_TOKEN, token)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(WEATHER_STEAM_USER_TOKEN, null)
    }

    fun isTokenExpired(): Boolean {
        val token = fetchAuthToken() ?: return true

        try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payload = parts[1]

            val payloadBytes = Base64.decode(payload, Base64.URL_SAFE)
            val payloadString = String(payloadBytes)

            val jsonObject = JSONObject(payloadString)
            if (!jsonObject.has("exp")) return true

            val expTimestamp = jsonObject.getLong("exp")

            val expInMillis = expTimestamp * 1000
            val currentTime = System.currentTimeMillis()

            return currentTime > expInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }

    fun logout() {
        prefs.edit(commit = true) {
            remove(WEATHER_STEAM_USER_TOKEN)
        }
    }
}
package com.example.weathersteam.data

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val username: String?,
    val token: String?
)

data class SteamLoginRequest(
    val username: String,
    @SerializedName("steam_id")
    val steamId: String
)
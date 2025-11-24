package com.example.weathersteam.data

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
    val user_id: Int?,
    val username: String?
)
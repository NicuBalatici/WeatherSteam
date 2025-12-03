package com.example.weathersteam.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

data class LoginResponse @OptIn(ExperimentalUuidApi::class) constructor(
    val success: Boolean,
    val message: String?,
    val username: String?,
    val token: String?
)
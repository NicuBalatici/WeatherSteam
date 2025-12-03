package com.example.weathersteam.network

import com.example.weathersteam.data.LoginRequest
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.data.RegisterRequest
import com.example.weathersteam.data.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @POST("api/register/")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
}
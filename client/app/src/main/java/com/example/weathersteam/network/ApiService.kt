package com.example.weathersteam.network

import com.example.weathersteam.data.GameAddRequest
import com.example.weathersteam.data.GameAddResponse
import com.example.weathersteam.data.GameResponse
import com.example.weathersteam.data.LoginRequest
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.data.RegisterRequest
import com.example.weathersteam.data.RegisterResponse
import com.example.weathersteam.data.SteamLoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @POST("api/register/")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
    @GET("api/game/user/")
    fun getUserGames(
        @Query("user_id") userId: String,
        @Query("weather") weather: String?,
        @Query("mood") mood: String?,
        @Query("pace") pace: String?,
        @Query("difficulty") difficulty: String?
    ): Call<GameResponse>
    @POST("api/game/add/")
    fun addGame(
        @Body request: GameAddRequest
    ): Call<GameAddResponse>
    @POST("api/login/steam/")
    fun steamLogin(@Body request: SteamLoginRequest): Call<LoginResponse>
}
package com.example.weathersteam.helpers

import com.example.weathersteam.BuildConfig
import com.example.weathersteam.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiNetworkClient {
    // USE 10.0.2.2 for Emulator, or your IP (192.168.x.x) for physical device
    private const val BASE_URL = BuildConfig.SERVER_BASE_ADDRESS

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
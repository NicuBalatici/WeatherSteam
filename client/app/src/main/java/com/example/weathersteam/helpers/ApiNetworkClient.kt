package com.example.weathersteam.helpers

import com.example.weathersteam.BuildConfig
import com.example.weathersteam.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiNetworkClient {
    private const val BASE_URL = BuildConfig.SERVER_BASE_ADDRESS

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
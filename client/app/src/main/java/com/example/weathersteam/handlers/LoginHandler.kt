package com.example.weathersteam.handlers

import android.util.Log
import com.example.weathersteam.data.LoginRequest
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginNetworkClient {
    // USE 10.0.2.2 for Emulator, or your IP (192.168.x.x) for physical device
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class LoginHandler {
    fun performLogin(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        val request = LoginRequest(email = email, password = pass)

        LoginNetworkClient.api.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Welcome ${response.body()?.username}!")
                } else {
                    val msg = response.body()?.message ?: "Login Failed"
                    onResult(false, msg)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", t.message.toString())
                onResult(false, "Connection Error: Check Server IP")
            }
        })
    }
}
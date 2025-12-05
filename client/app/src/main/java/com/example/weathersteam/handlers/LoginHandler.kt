package com.example.weathersteam.handlers

import android.content.Context
import android.util.Log
import com.example.weathersteam.data.LoginRequest
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.helpers.ApiNetworkClient
import com.example.weathersteam.helpers.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginHandler {
    fun performLogin(context: Context, email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        val request = LoginRequest(email = email, password = pass)
        val sessionManager = SessionManager(context)

        ApiNetworkClient.api.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.token
                    if (token != null) {
                        sessionManager.saveAuthToken(token)
                    }

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
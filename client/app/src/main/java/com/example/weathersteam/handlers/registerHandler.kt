package com.example.weathersteam.handlers

import android.util.Log
import com.example.weathersteam.data.RegisterRequest
import com.example.weathersteam.data.RegisterResponse
import com.example.weathersteam.handlers.LoginNetworkClient // Re-using the client from LoginHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterHandler {

    fun performRegistration(
        username: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val request = RegisterRequest(username = username, email = email, password = password)

        LoginNetworkClient.api.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Account Created! Please Login.")
                } else {
                    val msg = response.body()?.message ?: "Registration Failed"
                    onResult(false, msg)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterHandler", "Error: ${t.message}")
                onResult(false, "Connection Error: Check Server")
            }
        })
    }
}
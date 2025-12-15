package com.example.weathersteam.handlers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.weathersteam.BuildConfig
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.data.SteamLoginRequest
import com.example.weathersteam.helpers.ApiNetworkClient
import com.example.weathersteam.helpers.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SteamLoginHandler {
    companion object {
        suspend fun performLogin(
            context: Context?,
            formContent: String,
            onResult: (Boolean, String) -> Unit
        ) {
            val client = HttpClient(CIO)
            val steamIdPattern = "[0-9]+".toRegex()
            var steamId: String
            val steamKey = BuildConfig.STEAM_API_KEY

            if (steamIdPattern.containsMatchIn(formContent)) {
                steamId = formContent
            } else {
                val vanityUrl = URLBuilder(
                    "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/?key=${
                        steamKey
                    }&vanityurl=${formContent}"
                ).build()
                val response = client.get(vanityUrl)
                val responseJson = JSONObject(response.bodyAsText())
                    .getJSONObject("response")

                if (responseJson.getInt("success") != 1) {
                    Toast.makeText(context, "Error: user not found", Toast.LENGTH_LONG).show()
                    return
                }

                steamId = responseJson.getString("steamid")
            }

            val url = URLBuilder(
                "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=${
                    steamKey
                }&steamids=${steamId}"
            ).build()

            val response = client.get(url)
            val responseJson = JSONObject(response.bodyAsText())
                .getJSONObject("response")
            val player = responseJson
                .getJSONArray("players")
                .optJSONObject(0)

            if (player == null) {
                Toast.makeText(context, "Error: user not found", Toast.LENGTH_LONG).show()
                return
            }

            val username = player.getString("personaname")

            val request = SteamLoginRequest(username, steamId)
            val sessionManager = SessionManager(context!!)

            ApiNetworkClient.api.steamLogin(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
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

            client.close()
        }
    }
}
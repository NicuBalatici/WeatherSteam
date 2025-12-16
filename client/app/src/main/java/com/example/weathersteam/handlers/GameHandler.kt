package com.example.weathersteam.handlers

import com.example.weathersteam.data.Game
import com.example.weathersteam.data.GameAddRequest
import com.example.weathersteam.data.GameAddResponse
import com.example.weathersteam.helpers.ApiNetworkClient
import com.example.weathersteam.data.GameResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameHandler {
    fun fetchGame (
        userId: String,
        weather: String?,
        mood: String?,
        pace: String?,
        difficulty: String?,
        onResult: (Boolean, Game?, String) -> Unit
    ) {
        ApiNetworkClient.api.getUserGames(userId, weather, mood, pace, difficulty).enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val gamesList = response.body()?.games ?: emptyList()
                    print(gamesList)
                    if (gamesList.isNotEmpty()) {
                        val randomGame = gamesList.random()
                        onResult(true, randomGame, "Game found!")
                    } else {
                        onResult(false, null, "No games found")
                    }
                } else {
                    onResult(false, null, "Server Error")
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                onResult(false, null, t.message ?: "Error")
            }
        })
    }

    fun fetchAllUserGames (
        userId: String,
        onResult: (Boolean, List<Game>?, String) -> Unit
    ) {
        ApiNetworkClient.api.getUserGames(
            userId,
            weather = "",
            mood = "",
            pace = "",
            difficulty = ""
        ).enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val gamesList = response.body()?.games ?: emptyList()
                    onResult(true, gamesList, "Success")
                } else {
                    val msg = response.body()?.message ?: "Unknown Server Error"
                    onResult(false, null, msg)
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                android.util.Log.e("GameHandler", "Network Error", t)
                onResult(false, null, t.localizedMessage ?: "Connection Error")
            }
        })
    }

    fun addGame(
        steamGameId: Long,
        title: String,
        imageUrl: String,
        tags: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val requestBody = GameAddRequest(
            steamGameId = steamGameId,
            title = title,
            imageUrl = imageUrl,
            tags = tags
        )

        ApiNetworkClient.api.addGame(requestBody
        ).enqueue(object : Callback<GameAddResponse> {
            override fun onResponse(call: Call<GameAddResponse>, response: Response<GameAddResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Success")
                } else {
                    onResult(false, "Server Error")
                }
            }

            override fun onFailure(call: Call<GameAddResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error")
            }
        })
    }
}
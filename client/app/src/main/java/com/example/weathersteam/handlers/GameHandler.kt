package com.example.weathersteam.handlers

import com.example.weathersteam.data.Game
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
                    print(gamesList)
                    if (gamesList.isNotEmpty()) {
                        onResult(true, gamesList, "Game found!")
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
}
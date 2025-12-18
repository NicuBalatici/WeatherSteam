package com.example.weathersteam.handlers

import com.example.weathersteam.data.Game
import com.example.weathersteam.data.GameAddRequest
import com.example.weathersteam.data.GameAddResponse
import com.example.weathersteam.helpers.ApiNetworkClient
import com.example.weathersteam.data.GameListResponse
import com.example.weathersteam.data.GameResponse
import com.example.weathersteam.data.GameUserAddRequest
import com.example.weathersteam.data.GameUserAddResponse
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
        ApiNetworkClient.api.getUserGames(userId, weather, mood, pace, difficulty).enqueue(object : Callback<GameListResponse> {
            override fun onResponse(call: Call<GameListResponse>, response: Response<GameListResponse>) {
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

            override fun onFailure(call: Call<GameListResponse>, t: Throwable) {
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
        ).enqueue(object : Callback<GameListResponse> {
            override fun onResponse(call: Call<GameListResponse>, response: Response<GameListResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val gamesList = response.body()?.games ?: emptyList()
                    onResult(true, gamesList, "Success")
                } else {
                    val msg = response.body()?.message ?: "Unknown Server Error"
                    onResult(false, null, msg)
                }
            }

            override fun onFailure(call: Call<GameListResponse>, t: Throwable) {
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
        onResult: (Boolean, String, String?) -> Unit
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
                    onResult(true, "Success", response.body()?.gameId)
                } else {
                    onResult(false, "Server Error", null)
                }
            }

            override fun onFailure(call: Call<GameAddResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error", null)
            }
        })
    }

    fun addGameUser(
        userId: String,
        gameId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val requestBody = GameUserAddRequest (
            userId = userId,
            gameId = gameId
        )

        ApiNetworkClient.api.addGameUser(requestBody
        ).enqueue(object : Callback<GameUserAddResponse> {
            override fun onResponse(call: Call<GameUserAddResponse>, response: Response<GameUserAddResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Success")
                } else {
                    onResult(false, "Invalid request")
                }
            }

            override fun onFailure(call: Call<GameUserAddResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error")
            }
        })
    }

    fun getGameBySteamId(
        steamGameId: String,
        onResult: (Boolean, String, Game?) -> Unit
    ) {
        ApiNetworkClient.api.getGameBySteamId(
            steamGameId = steamGameId
        ).enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val game = response.body()?.game
                    onResult(true, "Success", game)
                } else {
                    val msg = response.body()?.message ?: "Unknown Server Error"
                    onResult(false, msg, null)
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                onResult(false, t.localizedMessage ?: "Connection Error", null)
            }
        })
    }
}
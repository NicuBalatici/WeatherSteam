package com.example.weathersteam.handlers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.weathersteam.BuildConfig
import com.example.weathersteam.data.LoginResponse
import com.example.weathersteam.data.SteamLoginRequest
import com.example.weathersteam.helpers.ApiNetworkClient
import com.example.weathersteam.helpers.SessionManager
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
data class TempSteamGame (
    val steamGameId: Long,
    val title: String
)

class SteamLoginHandler {
    companion object {
        suspend fun performLogin(
            context: Context?,
            formContent: String,
            onResult: (Boolean, String) -> Unit
        ) {
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

                val response = HttpClient(CIO).use { client ->
                    val response = client.get(vanityUrl)
                    response.bodyAsText()
                }

                val responseJson = JSONObject(response)
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

            val response = HttpClient(CIO).use { client ->
                val response = client.get(url)
                response.bodyAsText()
            }

            val responseJson = JSONObject(response)
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

                        CoroutineScope(Dispatchers.IO).launch {
                            buildGameList(context)
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

        private suspend fun buildGameList(context: Context) {
            val userId = SessionManager(context).fetchUserIdFromToken()
            val steamId = SessionManager(context).fetchSteamIdFromToken()
            val steamApiKey = BuildConfig.STEAM_API_KEY

            val url = URLBuilder(
                "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=${
                    steamApiKey
                }&steamid=${steamId}&format=json&include_appinfo=1"
            ).build()

            val response = HttpClient(CIO).use { client ->
                val response = client.get(url)
                response.bodyAsText()
            }

            val gamesArray = JSONObject(response)
                .getJSONObject("response")
                .getJSONArray("games")

            val steamGamesList = mutableListOf<TempSteamGame>()

            for (i in 0 until gamesArray.length()) {
                val item = gamesArray.getJSONObject(i)
                steamGamesList.add(
                    TempSteamGame(
                        steamGameId = item.getLong("appid"),
                        title = item.getString("name")
                    )
                )
            }

            GameHandler().fetchAllUserGames(userId) { success, gamesList, message ->
                if (success) {
                    val existingIds = gamesList?.map { it.steamGameId }?.toSet()
                    val missingGames = steamGamesList.filter { !existingIds?.contains(it.steamGameId)!! }
                    CoroutineScope(Dispatchers.IO).launch {
                        handleMissingGames(missingGames)
                    }
                } else {
                    if (gamesList == null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            handleMissingGames(steamGamesList)
                        }
                    }
                }
            }
        }

        private suspend fun handleMissingGames(
            games: List<TempSteamGame>,
        ) {
            val gameHandler = GameHandler()
            val aiHandler = GoogleAiHandler()

            val chunks = games.chunked(20)

            for (chunk in chunks) {
                try {
                    val titles = chunk.map { it.title }

                    Log.d("SteamDebug", "Asking AI for batch: $titles")

                    val tagsMap = aiHandler.getGameTags(titles)

                    for (game in chunk) {
                        val safeImageUrl = "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${game.steamGameId}/capsule_231x87.jpg"
                        val tags = tagsMap[game.title] ?: "N/A,N/A,N/A,N/A"

                        gameHandler.addGame(
                            steamGameId = game.steamGameId,
                            title = game.title,
                            imageUrl = safeImageUrl,
                            tags = tags
                        ) { _, _ -> }
                    }

                    kotlinx.coroutines.delay(2000)
                } catch (e: Exception) {
                    Log.e("SteamDebug", "Batch Error", e)
                }
            }
        }
    }
}
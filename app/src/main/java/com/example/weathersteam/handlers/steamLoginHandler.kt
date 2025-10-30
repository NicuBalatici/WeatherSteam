package com.example.weathersteam.handlers

import android.content.Context
import android.widget.Toast
import com.example.weathersteam.R
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import org.json.JSONObject

suspend fun steamLoginHandler(context: Context?, formContent: String) {
    val client = HttpClient(CIO)
    val steamIdPattern = "[0-9]+".toRegex()
    var steamId: String

    if (steamIdPattern.containsMatchIn(formContent)) {
        steamId = formContent
    } else {
        val vanityUrl = URLBuilder("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/?key=${context?.getString(R.string.STEAM_API_KEY)}&vanityurl=${formContent}").build()
        val response = client.get(vanityUrl)
        val responseJson = JSONObject(response.bodyAsText())
            .getJSONObject("response")

        if (responseJson.getInt("success") != 1) {
            Toast.makeText(context, "Error: user not found", Toast.LENGTH_LONG).show()
            return
        }

        steamId = responseJson.getString("steamid")
    }

    val url = URLBuilder("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=${context?.getString(R.string.STEAM_API_KEY)}&steamids=${steamId}").build()

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

    /**
     * TODO: implement API calls to store user in database
     * it will return a JTW token that will be stored on the device
     */
}
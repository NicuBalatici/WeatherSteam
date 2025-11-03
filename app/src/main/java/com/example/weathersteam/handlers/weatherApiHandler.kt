package com.example.weathersteam.handlers

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import org.json.JSONObject

suspend fun weatherApiHandler(latitude: Double, longitude: Double) {
    val client = HttpClient(CIO)

    val weatherUrl = URLBuilder("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,rain,cloud_cover,snowfall,precipitation").build()
    val response = client.get(weatherUrl)
    val responseJson = JSONObject(response.bodyAsText()).getJSONObject("current")

    /**
     * TODO: take relevant data from responseJson
     * TODO: store data in a WeatherData object (create the WeatherData class)
     * TODO: return the object
     */

    client.close()
}
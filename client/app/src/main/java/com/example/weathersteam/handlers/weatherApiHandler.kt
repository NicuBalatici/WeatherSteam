package com.example.weathersteam.handlers

import com.example.weathersteam.data.WeatherData
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import org.json.JSONObject

suspend fun weatherApiHandler(latitude: Double, longitude: Double): WeatherData {
    val client = HttpClient(CIO)

    try {
        val weatherUrl =
            URLBuilder("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m&current=temperature_2m,rain,snowfall,cloud_cover,wind_speed_10m,is_day").build()
        val response = client.get(weatherUrl)
        val responseJson = JSONObject(response.bodyAsText()).getJSONObject("current")

        val temp = responseJson.getDouble("temperature_2m")
        val rain = responseJson.getDouble("rain")
        val cloudCover = responseJson.getInt("cloud_cover")
        val snowfall = responseJson.getDouble("snowfall")
        val windSpeed = responseJson.getDouble("wind_speed_10m")
        val isDay = responseJson.getInt("is_day")

        return WeatherData(
            temperature = temp,
            rain = rain,
            cloudCover = cloudCover,
            snowfall = snowfall,
            windSpeed = windSpeed,
            isDay = isDay == 1
        )

    } finally {
        client.close()
    }
}
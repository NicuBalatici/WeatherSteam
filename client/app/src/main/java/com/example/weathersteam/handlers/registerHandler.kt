package com.example.weathersteam.handlers

import android.content.Context
import com.example.weathersteam.data.RegisterData
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType

import io.ktor.client.request.*
import io.ktor.http.*

suspend fun registerHandler(context: Context?, username: String, password: String, confirmPassword: String): RegisterData? {
    if (password != confirmPassword) {
        return null
    }

    val registerData = RegisterData(username, password)

    val jsonBody = """
    {
        "name": "${registerData.name}",
        "password": "${registerData.password}"
    }
    """.trimIndent()

    val client = HttpClient(CIO)
    val url = "http://10.149.66.235:8081/api/users"

    return try {
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }

        client.close()

        if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
            registerData
        } else {
            null
        }

    } catch (e: Exception) {
        client.close()
        null
    }
}
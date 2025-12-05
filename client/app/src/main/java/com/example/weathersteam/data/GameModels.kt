package com.example.weathersteam.data

import com.google.gson.annotations.SerializedName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class GameResponse(
    val success: Boolean,
    val message: String?, // In case of error
    val games: List<Game>?
)

data class Game @OptIn(ExperimentalUuidApi::class) constructor(
    @SerializedName("id") val id: String,
    @SerializedName("steam_game_id") val steamGameId: Long,
    val title: String,
    @SerializedName("image_url") val imageUrl: String,
    val tags: String
)
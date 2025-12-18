package com.example.weathersteam.handlers

import android.util.Log
import com.example.weathersteam.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import org.json.JSONObject

class GoogleAiHandler {
    private var model: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getGameTags(gameTitles: List<String>): Map<String, String> {
        if (gameTitles.isEmpty()) return emptyMap()
        val titlesString = gameTitles.joinToString("\", \"")

        val prompt = """
            You are a game tagging engine. I will give you a list of video games.
            For EACH game, pick exactly 4 tags from these lists:
            1. [RAIN, SUN, CLOUDS, STORM, SNOW]
            2. [DARK, LIGHT]
            3. [SLOW_PACED, FAST_PACED]
            4. [BEGINNER, INTERMEDIATE, ADVANCED, EXPERT]
            
            Return a JSON object where the Key is the Game Name and the Value is the string of tags.
            
            Example Input: ["Stardew Valley", "Doom"]
            Example Output: { "Stardew Valley": "SUN,LIGHT,SLOW_PACED,BEGINNER", "Doom": "STORM,DARK,FAST_PACED,EXPERT" }
            
            Here is the list of games to tag: ["$titlesString"]
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            var jsonText = response.text ?: "{}"

            if (jsonText.contains("```")) {
                jsonText = jsonText.replace("```json", "")
                    .replace("```", "")
                    .trim()
            }

            Log.d("Gemini response", jsonText)

            val jsonObject = JSONObject(jsonText)
            val resultMap = mutableMapOf<String, String>()

            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                resultMap[key] = jsonObject.getString(key)
            }
            resultMap

        } catch (e: Exception) {
            Log.e("GoogleAiHandler", "Batch AI Error", e)
            emptyMap()
        }
    }
}
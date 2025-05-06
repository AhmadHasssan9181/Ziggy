package com.noobdev.Zibby

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

// API interface for Gemini
interface GeminiApiService {
        @Headers("Content-Type: application/json")
        @POST("v1beta/models/gemini-pro:generateContent")
        fun generateContent(
                @Body request: GeminiRequest,
                @Query("key") apiKey: String
        ): Call<GeminiResponse>
}

// Request model
data class GeminiRequest(
        val contents: List<Content>
)

data class Content(
        val parts: List<Part>
)

data class Part(
        val text: String
)

// Response models
data class GeminiResponse(
        val candidates: List<Candidate>? = null
)

data class Candidate(
        val content: Content? = null
)

// Singleton for API service
object GeminiClient {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        private val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}

// Function to make an API call
fun api_call(apiKey: String, prompt: String, callback: (GeminiResponse?) -> Unit) {
        // Create request body
        val request = GeminiRequest(
                contents = listOf(
                        Content(
                                parts = listOf(
                                        Part(text = prompt)
                                )
                        )
                )
        )

        // Make API call
        GeminiClient.service.generateContent(request, apiKey).enqueue(object : Callback<GeminiResponse> {
                override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                        if (response.isSuccessful) {
                                callback(response.body())
                        } else {
                                println("Error: ${response.code()} - ${response.message()}")
                                callback(null)
                        }
                }

                override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                        println("Network error: ${t.message}")
                        callback(null)
                }
        })
}

// Function to parse response
fun response(geminiResponse: GeminiResponse?): String {
        return geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response"
}
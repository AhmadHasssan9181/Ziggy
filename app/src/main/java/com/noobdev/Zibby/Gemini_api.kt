package com.noobdev.Zibby

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

private const val TAG = "GeminiApiClient"
private const val BASE_URL = "https://generativelanguage.googleapis.com/"
private const val DEFAULT_TIMEOUT_SECONDS = 30L

/**
 * Main interface for Gemini API service
 */
interface GeminiApiService {
        @Headers("Content-Type: application/json")
        @POST("v1beta/models/gemini-pro:generateContent")
        fun generateContent(
                @Body request: GeminiRequest,
                @Query("key") apiKey: String
        ): Call<GeminiResponse>

        @Headers("Content-Type: application/json")
        @POST("v1beta/models/{model}:generateContent")
        fun generateContentWithModel(
                @retrofit2.http.Path("model") model: String,
                @Body request: GeminiRequest,
                @Query("key") apiKey: String
        ): Call<GeminiResponse>
}

/**
 * Request and response data classes
 */
data class GeminiRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null,
        val safetySettings: List<SafetySetting>? = null
)

data class GenerationConfig(
        val temperature: Float? = null,
        val topK: Int? = null,
        val topP: Float? = null,
        val maxOutputTokens: Int? = null,
        val stopSequences: List<String>? = null
)

data class SafetySetting(
        val category: String,
        val threshold: String
)

data class Content(
        val parts: List<Part>,
        val role: String? = null
)

data class Part(
        val text: String? = null,
        val inlineData: InlineData? = null
)

data class InlineData(
        val mimeType: String,
        val data: String
)

data class GeminiResponse(
        val candidates: List<Candidate>? = null,
        val promptFeedback: PromptFeedback? = null
)

data class Candidate(
        val content: Content? = null,
        val finishReason: String? = null,
        val safetyRatings: List<SafetyRating>? = null
)

data class SafetyRating(
        val category: String,
        val probability: String
)

data class PromptFeedback(
        val blockReason: String? = null,
        val safetyRatings: List<SafetyRating>? = null
)

/**
 * Result wrapper for API responses
 */
sealed class GeminiResult<out T> {
        data class Success<T>(val data: T) : GeminiResult<T>()
        data class Error(val exception: Exception, val message: String, val code: Int? = null) : GeminiResult<Nothing>()

        inline fun onSuccess(action: (T) -> Unit): GeminiResult<T> {
                if (this is Success) action(data)
                return this
        }

        inline fun onError(action: (message: String, exception: Exception) -> Unit): GeminiResult<T> {
                if (this is Error) action(message, exception)
                return this
        }
}

/**
 * Main Gemini API client class
 */
class GeminiApiClient private constructor(
        private var apiKey: String = "",
        private val httpClient: OkHttpClient,
        private val service: GeminiApiService,
        private val gson: Gson
) {
        companion object {
                private var INSTANCE: GeminiApiClient? = null

                @JvmStatic
                fun getInstance(): GeminiApiClient {
                        return INSTANCE ?: synchronized(this) {
                                INSTANCE ?: createClient().also { INSTANCE = it }
                        }
                }

                @JvmStatic
                fun init(apiKey: String) {
                        getInstance().setApiKey(apiKey)
                }

                private fun createClient(): GeminiApiClient {
                        val gson = GsonBuilder()
                                .setLenient()
                                .create()

                        val httpClient = OkHttpClient.Builder()
                                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                .build()

                        val retrofit = Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .client(httpClient)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build()

                        return GeminiApiClient(
                                httpClient = httpClient,
                                service = retrofit.create(GeminiApiService::class.java),
                                gson = gson
                        )
                }
        }

        /**
         * Configure the API key for all subsequent requests
         */
        fun setApiKey(apiKey: String) {
                this.apiKey = apiKey
        }

        /**
         * Save API key securely in SharedPreferences
         */
        fun saveApiKey(context: Context) {
                val sharedPrefs = context.getSharedPreferences("gemini_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("api_key", apiKey).apply()
        }

        /**
         * Load API key from SharedPreferences
         */
        fun loadApiKey(context: Context): Boolean {
                val sharedPrefs = context.getSharedPreferences("gemini_prefs", Context.MODE_PRIVATE)
                val savedKey = sharedPrefs.getString("api_key", null)
                if (savedKey != null) {
                        this.apiKey = savedKey
                        return true
                }
                return false
        }

        /**
         * Check if API key is set
         */
        fun isApiKeySet() = apiKey.isNotBlank()

        /**
         * Clear the API key
         */
        fun clearApiKey(context: Context? = null) {
                apiKey = ""
                context?.let {
                        val sharedPrefs = it.getSharedPreferences("gemini_prefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit().remove("api_key").apply()
                }
        }

        /**
         * Simple text completion with callback
         */
        fun generateText(
                prompt: String,
                callback: (GeminiResult<String>) -> Unit
        ) {
                if (!isApiKeySet()) {
                        callback(GeminiResult.Error(
                                Exception("API key not set"),
                                "Please set your API key using GeminiApiClient.init(apiKey) before making requests"
                        ))
                        return
                }

                val request = GeminiRequest(
                        contents = listOf(
                                Content(
                                        parts = listOf(Part(text = prompt))
                                )
                        )
                )

                service.generateContent(request, apiKey).enqueue(object : Callback<GeminiResponse> {
                        override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                                try {
                                        if (response.isSuccessful) {
                                                val body = response.body()
                                                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                                                if (text != null) {
                                                        callback(GeminiResult.Success(text))
                                                } else {
                                                        val errorBody = response.errorBody()?.string() ?: "Empty response"
                                                        Log.e(TAG, "API returned empty response: $errorBody")
                                                        callback(GeminiResult.Error(
                                                                Exception("Empty response"),
                                                                "API returned empty response",
                                                                response.code()
                                                        ))
                                                }
                                        } else {
                                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                                Log.e(TAG, "API error: ${response.code()} - $errorBody")
                                                callback(GeminiResult.Error(
                                                        Exception(errorBody),
                                                        "API error: ${response.code()}",
                                                        response.code()
                                                ))
                                        }
                                } catch (e: Exception) {
                                        Log.e(TAG, "Error processing response", e)
                                        callback(GeminiResult.Error(e, "Error processing response"))
                                }
                        }

                        override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                                Log.e(TAG, "Network error", t)
                                callback(GeminiResult.Error(
                                        Exception(t),
                                        "Network error: ${t.message ?: "Unknown network error"}"
                                ))
                        }
                })
        }

        /**
         * Advanced text generation with configuration options and callback
         */
        fun generateTextAdvanced(
                prompt: String,
                model: GeminiModel = GeminiModel.GEMINI_PRO,
                temperature: Float? = null,
                maxTokens: Int? = null,
                callback: (GeminiResult<GeminiResponse>) -> Unit
        ) {
                if (!isApiKeySet()) {
                        callback(GeminiResult.Error(
                                Exception("API key not set"),
                                "Please set your API key using GeminiApiClient.init(apiKey) before making requests"
                        ))
                        return
                }

                val request = GeminiRequest(
                        contents = listOf(
                                Content(
                                        parts = listOf(Part(text = prompt))
                                )
                        ),
                        generationConfig = GenerationConfig(
                                temperature = temperature,
                                maxOutputTokens = maxTokens
                        )
                )

                service.generateContentWithModel(model.modelName, request, apiKey).enqueue(object : Callback<GeminiResponse> {
                        override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                                try {
                                        if (response.isSuccessful) {
                                                val body = response.body()
                                                if (body != null) {
                                                        callback(GeminiResult.Success(body))
                                                } else {
                                                        val errorBody = response.errorBody()?.string() ?: "Empty response"
                                                        Log.e(TAG, "API returned empty response: $errorBody")
                                                        callback(GeminiResult.Error(
                                                                Exception("Empty response"),
                                                                "API returned empty response",
                                                                response.code()
                                                        ))
                                                }
                                        } else {
                                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                                Log.e(TAG, "API error: ${response.code()} - $errorBody")
                                                callback(GeminiResult.Error(
                                                        Exception(errorBody),
                                                        "API error: ${response.code()}",
                                                        response.code()
                                                ))
                                        }
                                } catch (e: Exception) {
                                        Log.e(TAG, "Error processing response", e)
                                        callback(GeminiResult.Error(e, "Error processing response"))
                                }
                        }

                        override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                                Log.e(TAG, "Network error", t)
                                callback(GeminiResult.Error(
                                        Exception(t),
                                        "Network error: ${t.message ?: "Unknown network error"}"
                                ))
                        }
                })
        }

        /**
         * Suspending function for coroutines to generate text
         */
        suspend fun generateTextAsync(
                prompt: String,
                model: GeminiModel = GeminiModel.GEMINI_PRO,
                temperature: Float? = null,
                maxTokens: Int? = null
        ): GeminiResult<String> = withContext(Dispatchers.IO) {
                try {
                        if (!isApiKeySet()) {
                                return@withContext GeminiResult.Error(
                                        Exception("API key not set"),
                                        "Please set your API key using GeminiApiClient.init(apiKey) before making requests"
                                )
                        }

                        val request = GeminiRequest(
                                contents = listOf(
                                        Content(
                                                parts = listOf(Part(text = prompt))
                                        )
                                ),
                                generationConfig = GenerationConfig(
                                        temperature = temperature,
                                        maxOutputTokens = maxTokens
                                )
                        )

                        val response = suspendCancellableCoroutine { continuation ->
                                service.generateContentWithModel(model.modelName, request, apiKey)
                                        .enqueue(object : Callback<GeminiResponse> {
                                                override fun onResponse(
                                                        call: Call<GeminiResponse>,
                                                        response: Response<GeminiResponse>
                                                ) {
                                                        if (continuation.isActive) {
                                                                if (response.isSuccessful) {
                                                                        val text = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                                                        if (text != null) {
                                                                                continuation.resume(GeminiResult.Success(text))
                                                                        } else {
                                                                                continuation.resume(
                                                                                        GeminiResult.Error(
                                                                                                Exception("Empty response"),
                                                                                                "API returned empty response",
                                                                                                response.code()
                                                                                        )
                                                                                )
                                                                        }
                                                                } else {
                                                                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                                                        continuation.resume(
                                                                                GeminiResult.Error(
                                                                                        Exception(errorBody),
                                                                                        "API error: ${response.code()}",
                                                                                        response.code()
                                                                                )
                                                                        )
                                                                }
                                                        }
                                                }

                                                override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                                                        if (continuation.isActive) {
                                                                continuation.resume(
                                                                        GeminiResult.Error(
                                                                                Exception(t),
                                                                                "Network error: ${t.message ?: "Unknown network error"}"
                                                                        )
                                                                )
                                                        }
                                                }
                                        })
                        }

                        return@withContext response
                } catch (e: Exception) {
                        return@withContext GeminiResult.Error(e, "Exception during API call: ${e.message}")
                }
        }

        /**
         * Parse raw text from a GeminiResponse
         */
        fun parseTextFromResponse(response: GeminiResponse?): String {
                return response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response"
        }
}

/**
 * Gemini models available
 */
enum class GeminiModel(val modelName: String) {
        GEMINI_PRO("gemini-pro"),
        GEMINI_PRO_VISION("gemini-pro-vision"),
        GEMINI_ULTRA("gemini-ultra") // May require specific access
}

/**
 * Extension functions for Activity/Fragment usage
 */
fun Context.initGeminiApi(apiKey: String) {
        GeminiApiClient.init(apiKey)
        GeminiApiClient.getInstance().saveApiKey(this)
}

fun Context.loadGeminiApiKey(): Boolean {
        return GeminiApiClient.getInstance().loadApiKey(this)
}

/**
 * Simple API for backward compatibility
 */
object GeminiApi {
        /**
         * Initialize with API key
         */
        fun init(apiKey: String) {
                GeminiApiClient.init(apiKey)
        }

        /**
         * Legacy API call function with simplified callback
         */
        fun api_call(prompt: String, callback: (String?) -> Unit) {
                GeminiApiClient.getInstance().generateText(prompt) { result ->
                        when (result) {
                                is GeminiResult.Success -> callback(result.data)
                                is GeminiResult.Error -> {
                                        Log.e(TAG, "Error: ${result.message}", result.exception)
                                        callback(null)
                                }
                        }
                }
        }

        /**
         * Legacy response parser
         */
        fun response(geminiResponse: GeminiResponse?): String {
                return GeminiApiClient.getInstance().parseTextFromResponse(geminiResponse)
        }
}
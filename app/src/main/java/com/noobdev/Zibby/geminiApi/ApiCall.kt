package com.noobdev.Zibby.geminiApi


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Travel Planner API Client
 * Updated implementation including all endpoints
 * Created for AhmadHasssan9181
 * Last updated: 2025-05-15
 */

// =================== DATA MODELS ===================

// Budget Generation Models
data class BudgetRequest(
    val destination: String,
    val duration_days: Int,
    val travelers: Int,
    val budget_level: String,
    val include_flight: Boolean = true,
    val include_accommodation: Boolean = true,
    val include_activities: Boolean = true,
    val include_food: Boolean = true,
    val origin: String? = null,
    val currency: String = "USD"
)

data class BudgetResponse(
    val total_budget: Int,
    val breakdown: Map<String, Int>,
    val tips: List<String>,
    val currency: String
)

// Trip Planning Models
data class TripPlanRequest(
    val destination: String,
    val duration_days: Int,
    val interests: List<String>,
    val travel_style: String,
    val travel_dates: String? = null,
    val include_accommodation: Boolean = true,
    val include_transportation: Boolean = true
)

data class TripPlanResponse(
    val destination_overview: String,
    val daily_itinerary: List<DailyItineraryItem>,
    val recommended_places: List<RecommendedPlace>,
    val transportation_tips: List<String>,
    val accommodation_suggestions: List<AccommodationSuggestion>
) {
    data class DailyItineraryItem(
        val day: Int,
        val activities: List<Activity>,
        val meals: List<Meal>,
        val notes: String? = null
    )

    data class Activity(
        val time: String,
        val name: String,
        val description: String,
        val location: String? = null,
        val duration: String? = null,
        val cost: String? = null
    )

    data class Meal(
        val type: String,
        val suggestion: String,
        val location: String? = null
    )

    data class RecommendedPlace(
        val name: String,
        val type: String,
        val description: String,
        val location: String? = null,
        val rating: Double? = null,
        val image_url: String? = null
    )

    data class AccommodationSuggestion(
        val name: String,
        val type: String,
        val price_range: String,
        val location: String? = null,
        val amenities: List<String>? = null,
        val rating: Double? = null,
        val description: String? = null,
        val image_url: String? = null
    )
}

// Travel Advice Models
data class TravelAdviceRequest(
    val destination: String,
    val travel_dates: String? = null,
    val duration_days: Int? = null,
    val specific_questions: List<String>? = null
)

data class TravelAdviceResponse(
    val weather_info: String,
    val local_customs: List<String>,
    val safety_tips: List<String>,
    val packing_suggestions: List<String>,
    val visa_requirements: String,
    val currency_info: String,
    val health_recommendations: List<String>,
    val answers_to_questions: Map<String, String>
)

// Hotel Search Models
data class HotelSearchRequest(
    val location: String,
    val check_in_date: String,
    val check_out_date: String,
    val guests: Int = 2
)

data class HotelSearchResponse(
    val search_info: SearchInfo,
    val hotels: List<Hotel>,
    val price_range: PriceRange,
    val search_metadata: SearchMetadata,
    val exchange_rates: Map<String, Double>,
    val coordinates: Map<String, Double>
) {
    data class SearchInfo(
        val location: String,
        val check_in: String,
        val check_out: String,
        val guests: Int,
        val search_date: String
    )

    data class Hotel(
        val name: String,
        val address: String,
        val rating: Double,
        val price: String,
        val description: String,
        val amenities: List<String>,
        val image_url: String? = null,
        val reviews_count: Int? = null,
        val availability: String? = null
    )

    data class PriceRange(
        val budget: String,
        val mid_range: String,
        val luxury: String
    )

    data class SearchMetadata(
        val search_suggestions: List<String>,
        val rendered_content: String
    )
}

// Destination Info Models
data class DestinationRequest(
    val location: String
)

data class DestinationResponse(
    val name: String,
    val country: String,
    val description: String,
    val highlights: List<String>,
    val best_time_to_visit: String,
    val language: String,
    val currency: String,
    val time_zone: String,
    val weather_summary: String,
    val safety_index: String,
    val cost_level: String,
    val photos: List<String>,
    val local_phrases: Map<String, String>
)

// Weather Models
data class WeatherResponse(
    val location: String,
    val temperature: Temperature,
    val condition: String,
    val humidity: Int,
    val wind: Wind,
    val forecast: List<ForecastDay>,
    val updated_at: String,
    val search_metadata: SearchMetadata
) {
    data class Temperature(
        val celsius: Double,
        val fahrenheit: Double
    )

    data class Wind(
        val speed: Double,
        val direction: String,
        val unit: String
    )

    data class ForecastDay(
        val date: String,
        val condition: String,
        val high_temp: Temperature,
        val low_temp: Temperature,
        val precipitation_chance: Int,
        val sunrise: String,
        val sunset: String
    )

    data class SearchMetadata(
        val search_suggestions: List<String>,
        val rendered_content: String
    )
}

// Attractions Models
data class AttractionResponse(
    val location: String,
    val attractions: List<Attraction>,
    val total_found: Int,
    val radius_used: Int,
    val coordinates: Coordinates
) {
    data class Attraction(
        val name: String,
        val type: String,
        val description: String,
        val rating: Double,
        val address: String,
        val distance: Double,
        val image_url: String?,
        val website: String?,
        val opening_hours: String?
    )

    data class Coordinates(
        val latitude: Double,
        val longitude: Double
    )
}

// Exchange Rates Model
data class ExchangeRatesResponse(
    val base_currency: String,
    val rates: Map<String, Double>,
    val updated_at: String
)

// Chatbot Models
data class ChatbotRequest(
    val message: String
)

data class ChatbotResponse(
    val response: String,
    val video_used: String
)

// YouTube Models
data class YouTubeSearchResponse(
    val videos: List<YouTubeVideo>
) {
    data class YouTubeVideo(
        val id: String,
        val title: String,
        val description: String,
        val thumbnail_url: String,
        val channel_name: String,
        val published_at: String,
        val view_count: String,
        val duration: String,
        val url: String
    )
}

// Root Response
data class RootResponse(
    val api_name: String,
    val version: String,
    val status: String,
    val documentation_url: String,
    val timestamp: String
)

// =================== API INTERFACE ===================

interface TravelApiService {
    // Budget Endpoints
    @POST("/budget/generate")
    suspend fun generateBudget(@Body request: BudgetRequest): Response<BudgetResponse>

    // Trip Planning Endpoints
    @POST("/trip/plan")
    suspend fun generateTripPlan(@Body request: TripPlanRequest): Response<TripPlanResponse>

    // Travel Advice Endpoints
    @POST("/travel/advice")
    suspend fun generateTravelAdvice(@Body request: TravelAdviceRequest): Response<TravelAdviceResponse>

    // Hotel Search Endpoints
    @POST("/search/hotels")
    suspend fun searchHotels(@Body request: HotelSearchRequest): Response<HotelSearchResponse>

    // Destination Info Endpoints
    @POST("/search/destination")
    suspend fun getDestinationInfo(@Body request: DestinationRequest): Response<DestinationResponse>

    // Weather Endpoints
    @GET("/search/weather/{location}")
    suspend fun getWeather(@Path("location") location: String): Response<WeatherResponse>

    // Attraction Endpoints
    @GET("/search/attractions/{location}")
    suspend fun getAttractions(
        @Path("location") location: String,
        @Query("radius") radius: Int = 10000,
        @Query("limit") limit: Int = 20
    ): Response<AttractionResponse>

    // Exchange Rate Endpoints
    @GET("/search/exchange-rates")
    suspend fun getExchangeRates(@Query("base_currency") baseCurrency: String = "USD"): Response<ExchangeRatesResponse>

    // Chatbot Endpoints
    @POST("/chatbot/chat")
    suspend fun chat(@Body request: ChatbotRequest): Response<ChatbotResponse>

    @POST("/chatbot/chat/context")
    suspend fun chatWithContext(@Body request: ChatbotRequest): Response<ChatbotResponse>

    @GET("/chatbot/health")
    suspend fun checkChatbotHealth(): Response<Map<String, String>>

    // YouTube Endpoints
    @GET("/youtube/")
    suspend fun getYouTubeInfo(): Response<Map<String, Any>>

    @GET("/youtube/search")
    suspend fun searchYouTube(
        @Query("query") query: String,
        @Query("max_results") maxResults: Int = 10
    ): Response<YouTubeSearchResponse>

    @GET("/youtube/search-and-save")
    suspend fun searchAndSaveYouTube(
        @Query("query") query: String,
        @Query("max_results") maxResults: Int = 10,
        @Query("filename") filename: String = "youtube_results.json"
    ): Response<Map<String, Any>>

    // Root Endpoint
    @GET("/")
    suspend fun getRoot(): Response<RootResponse>
}

// =================== RETROFIT CLIENT ===================

object RetrofitClient {
    private const val BASE_URL = "https://travel-planner-api-c9fd03b114f8.herokuapp.com"
    private const val TAG = "TravelPlannerAPI"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val travelApiService: TravelApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelApiService::class.java)
    }
}

// =================== REPOSITORY ===================

class TravelRepository {
    private val apiService = RetrofitClient.travelApiService
    private val tag = "TravelRepository"

    // Budget Generation
    suspend fun generateBudget(
        destination: String,
        durationDays: Int,
        travelers: Int,
        budgetLevel: String,
        includeFlights: Boolean = true,
        includeAccommodation: Boolean = true,
        includeActivities: Boolean = true,
        includeFood: Boolean = true,
        origin: String? = null,
        currency: String = "USD"
    ): Result<BudgetResponse> = withContext(Dispatchers.IO) {
        try {
            val request = BudgetRequest(
                destination = destination,
                duration_days = durationDays,
                travelers = travelers,
                budget_level = budgetLevel,
                include_flight = includeFlights,
                include_accommodation = includeAccommodation,
                include_activities = includeActivities,
                include_food = includeFood,
                origin = origin,
                currency = currency
            )

            Log.d(tag, "Generating budget for $destination")
            val response = apiService.generateBudget(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Budget generated successfully: ${it.total_budget} ${it.currency}")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception generating budget: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Trip Planning
    suspend fun generateTripPlan(
        destination: String,
        durationDays: Int,
        interests: List<String>,
        travelStyle: String,
        travelDates: String? = null,
        includeAccommodation: Boolean = true,
        includeTransportation: Boolean = true
    ): Result<TripPlanResponse> = withContext(Dispatchers.IO) {
        try {
            val request = TripPlanRequest(
                destination = destination,
                duration_days = durationDays,
                interests = interests,
                travel_style = travelStyle,
                travel_dates = travelDates,
                include_accommodation = includeAccommodation,
                include_transportation = includeTransportation
            )

            Log.d(tag, "Generating trip plan for $destination")
            val response = apiService.generateTripPlan(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Trip plan generated successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception generating trip plan: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Travel Advice
    suspend fun generateTravelAdvice(
        destination: String,
        travelDates: String? = null,
        durationDays: Int? = null,
        specificQuestions: List<String>? = null
    ): Result<TravelAdviceResponse> = withContext(Dispatchers.IO) {
        try {
            val request = TravelAdviceRequest(
                destination = destination,
                travel_dates = travelDates,
                duration_days = durationDays,
                specific_questions = specificQuestions
            )

            Log.d(tag, "Generating travel advice for $destination")
            val response = apiService.generateTravelAdvice(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Travel advice generated successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception generating travel advice: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Hotel Search
    suspend fun searchHotels(
        location: String,
        checkInDate: String,
        checkOutDate: String,
        guests: Int = 2
    ): Result<HotelSearchResponse> = withContext(Dispatchers.IO) {
        try {
            val request = HotelSearchRequest(
                location = location,
                check_in_date = checkInDate,
                check_out_date = checkOutDate,
                guests = guests
            )

            Log.d(tag, "Searching hotels in $location")
            val response = apiService.searchHotels(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Hotel search successful, found ${it.hotels.size} hotels")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception searching hotels: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Destination Info
    suspend fun getDestinationInfo(
        location: String
    ): Result<DestinationResponse> = withContext(Dispatchers.IO) {
        try {
            val request = DestinationRequest(location = location)

            Log.d(tag, "Getting destination info for $location")
            val response = apiService.getDestinationInfo(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Destination info retrieved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting destination info: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Weather
    suspend fun getWeather(location: String): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting weather for $location")
            val response = apiService.getWeather(location)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Weather retrieved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting weather: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Attractions
    suspend fun getAttractions(
        location: String,
        radius: Int = 10000,
        limit: Int = 20
    ): Result<AttractionResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting attractions for $location")
            val response = apiService.getAttractions(location, radius, limit)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Attractions retrieved successfully, count: ${it.attractions.size}")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting attractions: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Exchange Rates
    suspend fun getExchangeRates(baseCurrency: String = "USD"): Result<ExchangeRatesResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting exchange rates for base currency $baseCurrency")
            val response = apiService.getExchangeRates(baseCurrency)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Exchange rates retrieved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting exchange rates: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Chatbot
    suspend fun chat(message: String): Result<ChatbotResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ChatbotRequest(message = message)
            Log.d(tag, "Sending message to chatbot")
            val response = apiService.chat(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Chatbot response received")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in chatbot: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun chatWithContext(message: String): Result<ChatbotResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ChatbotRequest(message = message)
            Log.d(tag, "Sending contextual message to chatbot")
            val response = apiService.chatWithContext(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Contextual chatbot response received")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in contextual chatbot: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun checkChatbotHealth(): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Checking chatbot health")
            val response = apiService.checkChatbotHealth()

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Chatbot health checked successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception checking chatbot health: ${e.message}", e)
            Result.failure(e)
        }
    }

    // YouTube
    suspend fun getYouTubeInfo(): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting YouTube API info")
            val response = apiService.getYouTubeInfo()

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "YouTube API info retrieved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting YouTube API info: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun searchYouTube(
        query: String,
        maxResults: Int = 10
    ): Result<YouTubeSearchResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Searching YouTube videos for query: $query, max results: $maxResults")
            val response = apiService.searchYouTube(query, maxResults)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "YouTube videos retrieved successfully, count: ${it.videos.size}")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception searching YouTube: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun searchAndSaveYouTube(
        query: String,
        maxResults: Int = 10,
        filename: String = "youtube_results.json"
    ): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Searching and saving YouTube videos for query: $query")
            val response = apiService.searchAndSaveYouTube(query, maxResults, filename)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "YouTube videos searched and saved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception searching and saving YouTube videos: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Root Endpoint
    suspend fun getRoot(): Result<RootResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting API root information")
            val response = apiService.getRoot()

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "API root information retrieved successfully")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception getting API root information: ${e.message}", e)
            Result.failure(e)
        }
    }
}

// =================== USAGE EXAMPLES ===================

/**
 * Example usage in a ViewModel:
 *
 * class TravelViewModel : ViewModel() {
 *     private val repository = TravelRepository()
 *
 *     private val _budgetResult = MutableLiveData<BudgetResponse>()
 *     val budgetResult: LiveData<BudgetResponse> = _budgetResult
 *
 *     fun generateBudget(...) {
 *         viewModelScope.launch {
 *             repository.generateBudget(...).fold(
 *                 onSuccess = { _budgetResult.value = it },
 *                 onFailure = { handleError(it) }
 *             )
 *         }
 *     }
 * }
 *
 * Example complete usage in Activity/Fragment:
 *
 * // In your Activity or Fragment
 * private val viewModel: TravelViewModel by viewModels()
 *
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     setContentView(R.layout.activity_main)
 *
 *     setupObservers()
 *     setupClickListeners()
 * }
 *
 * private fun setupObservers() {
 *     viewModel.budgetResult.observe(this) { budget ->
 *         // Update UI with budget info
 *         binding.totalBudget.text = "${budget.total_budget} ${budget.currency}"
 *     }
 *
 *     viewModel.error.observe(this) { errorMessage ->
 *         Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
 *     }
 * }
 *
 * private fun setupClickListeners() {
 *     binding.generateBudgetButton.setOnClickListener {
 *         viewModel.generateBudget(
 *             destination = binding.destinationInput.text.toString(),
 *             durationDays = binding.durationInput.text.toString().toInt(),
 *             travelers = binding.travelersInput.text.toString().toInt(),
 *             budgetLevel = when {
 *                 binding.economyRadio.isChecked -> "economy"
 *                 binding.luxuryRadio.isChecked -> "luxury"
 *                 else -> "mid-range"
 *             }
 *         )
 *     }
 * }
 */
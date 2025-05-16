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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
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
    val total_budget: Int = 0,
    val breakdown: Map<String, Int>? = emptyMap(),
    val tips: List<String>? = emptyList(),
    val currency: String = "USD"
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
    val destination_overview: String = "",
    val daily_itinerary: List<DailyItineraryItem> = emptyList(),
    val recommended_places: List<RecommendedPlace> = emptyList(),
    val transportation_tips: List<String> = emptyList(),
    val accommodation_suggestions: List<AccommodationSuggestion> = emptyList()
) {
    data class DailyItineraryItem(
        val day: Int = 1,
        val activities: List<Activity> = emptyList(),
        val meals: List<Meal> = emptyList(),
        val notes: String? = null
    )

    data class Activity(
        val time: String = "",
        val name: String = "",
        val description: String = "",
        val location: String? = null,
        val duration: String? = null,
        val cost: String? = null
    )

    data class Meal(
        val type: String = "",
        val suggestion: String = "",
        val location: String? = null
    )

    data class RecommendedPlace(
        val name: String = "",
        val type: String = "",
        val description: String = "",
        val location: String? = null,
        val rating: Double? = null,
        val image_url: String? = null
    )

    data class AccommodationSuggestion(
        val name: String = "",
        val type: String = "",
        val price_range: String = "",
        val location: String? = null,
        val amenities: List<String>? = emptyList(),
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
    val weather_info: String = "",
    val local_customs: List<String> = emptyList(),
    val safety_tips: List<String> = emptyList(),
    val packing_suggestions: List<String> = emptyList(),
    val visa_requirements: String = "",
    val currency_info: String = "",
    val health_recommendations: List<String> = emptyList(),
    val answers_to_questions: Map<String, String> = emptyMap()
)

// Hotel Search Models
data class HotelSearchRequest(
    val location: String,
    val check_in_date: String,
    val check_out_date: String,
    val guests: Int = 2
)

data class HotelSearchResponse(
    val search_info: SearchInfo = SearchInfo(),
    val hotels: List<Hotel> = emptyList(),
    val price_range: PriceRange = PriceRange(),
    val search_metadata: SearchMetadata = SearchMetadata(),
    val exchange_rates: Map<String, Double> = emptyMap(),
    val coordinates: Map<String, Double> = emptyMap()
) {
    data class SearchInfo(
        val location: String = "",
        val check_in: String = "",
        val check_out: String = "",
        val guests: Int = 0,
        val search_date: String = ""
    )

    data class Hotel(
        val name: String = "",
        val address: String = "",
        val rating: Double = 0.0,
        val price: String = "",
        val description: String = "",
        val amenities: List<String> = emptyList(),
        val image_url: String? = null,
        val reviews_count: Int? = null,
        val availability: String? = null
    )

    data class PriceRange(
        val budget: String = "",
        val mid_range: String = "",
        val luxury: String = ""
    )

    data class SearchMetadata(
        val search_suggestions: List<String> = emptyList(),
        val rendered_content: String = ""
    )
}

// Destination Info Models
// Updated Destination Info Models
data class DestinationRequest(
    val location: String
)

data class DestinationResponse(
    val destination: String = "",
    val timestamp: String = "",
    val coordinates: Coordinates = Coordinates(),
    val destination_info: DestinationInfo = DestinationInfo(),
    val weather: Weather = Weather(),
    val exchange_rates: Map<String, Double> = emptyMap(),
    val attractions: List<Attraction> = emptyList()
) {
    data class Coordinates(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    data class DestinationInfo(
        val overview: String = "",
        val best_time_to_visit: BestTimeToVisit = BestTimeToVisit(),
        val local_customs: List<String> = emptyList(),
        val transportation: Transportation = Transportation(),
        val safety_tips: List<String> = emptyList(),
        val must_see: List<String> = emptyList(),
        val food_recommendations: List<FoodRecommendation> = emptyList(),
        val search_metadata: SearchMetadata = SearchMetadata()
    ) {
        data class BestTimeToVisit(
            val peak_season: String = "",
            val shoulder_season: String = "",
            val off_season: String = "",
            val current_season: String = ""
        )

        data class Transportation(
            val from_airport: String = "",
            val within_city: String = "",
            val regional: String = ""
        )

        data class FoodRecommendation(
            val name: String = "",
            val cuisine: String = "",
            val price_range: String = ""
        )

        data class SearchMetadata(
            val search_suggestions: List<String> = emptyList(),
            val rendered_content: String = ""
        )
    }

    data class Weather(
        val location: String = "",
        val temperature: Temperature = Temperature(),
        val condition: String = "",
        val humidity: Int = 0,
        val wind: Wind = Wind(),
        val forecast: List<WeatherForecast> = emptyList(),
        val updated_at: String = "",
        val search_metadata: SearchMetadata = SearchMetadata()
    ) {
        data class Temperature(
            val celsius: Double = 0.0,
            val fahrenheit: Double = 0.0
        )

        data class Wind(
            val speed: Double? = 0.0,
            val unit: String = "",
            val direction: String? = ""
        )

        data class WeatherForecast(
            val time: String = "",
            val condition: String = "",
            val temperature: Temperature = Temperature()
        )

        data class SearchMetadata(
            val search_suggestions: List<String> = emptyList(),
            val rendered_content: String = ""
        )
    }

    data class Attraction(
        val name: String = "",
        val type: String = "",
        val description: String = "",
        val rating: Double = 0.0,
        val address: String = "",
        val distance: Double = 0.0
    )

    // For backward compatibility with your UI
    val name: String get() = destination
    val country: String get() = ""  // Could extract from destination if needed
    val description: String get() = destination_info.overview
    val highlights: List<String> get() = destination_info.must_see
    val best_time_to_visit: String get() = formatBestTimeToVisit()
    val language: String get() = ""  // Not directly available in the response
    val currency: String get() = ""  // Not directly available in the response
    val time_zone: String get() = ""  // Not directly available in the response
    val weather_summary: String get() = weather.condition
    val safety_index: String get() = ""  // Not directly available in the response
    val cost_level: String get() = ""  // Not directly available in the response
    val photos: List<String> get() = emptyList()  // Not directly available in the response
    val local_phrases: Map<String, String> get() = emptyMap()  // Not directly available in the response

    private fun formatBestTimeToVisit(): String {
        val bestTime = destination_info.best_time_to_visit
        return buildString {
            if (bestTime.peak_season.isNotEmpty()) {
                append("Peak season: ")
                append(bestTime.peak_season)
                append("\n\n")
            }
            if (bestTime.shoulder_season.isNotEmpty()) {
                append("Shoulder season: ")
                append(bestTime.shoulder_season)
                append("\n\n")
            }
            if (bestTime.off_season.isNotEmpty()) {
                append("Off season: ")
                append(bestTime.off_season)
                append("\n\n")
            }
            if (bestTime.current_season.isNotEmpty()) {
                append("Current season: ")
                append(bestTime.current_season)
            }
        }.trim()
    }
}
// Weather Models
data class WeatherResponse(
    val location: String = "",
    val temperature: Temperature = Temperature(),
    val condition: String = "",
    val humidity: Double = 0.0, // Changed from Int to Double to match API
    val wind: Wind = Wind(),
    val forecast: List<ForecastTime> = emptyList(), // Changed from ForecastDay to ForecastTime
    val updated_at: String = "",
    val search_metadata: SearchMetadata = SearchMetadata()
) {
    data class Temperature(
        val celsius: Double = 0.0,
        val fahrenheit: Double = 0.0
    )

    data class Wind(
        val speed: Double? = 0.0,
        val direction: String? = "",
        val unit: String? = ""
    )

    // Updated to match the actual API response structure
    data class ForecastTime(
        val time: String = "",        // Using time instead of date
        val condition: String = "",
        val temperature: Temperature = Temperature()
        // Note: No high_temp, low_temp, precipitation_chance, sunrise, sunset
    )

    data class SearchMetadata(
        val search_suggestions: List<String> = emptyList(),
        val rendered_content: String = ""
    )
}

data class AttractionResponse(
    val features: List<Feature> = emptyList(),
    // For backward compatibility
    val location: String = "",
    val total_found: Int = 0,
    val radius_used: Int = 0,
    val coordinates: Coordinates = Coordinates()
) {
    data class Feature(
        val xid: String = "",
        val name: String = "",
        val dist: Double = 0.0,
        val rate: Int = 0,
        val osm: String? = null,
        val wikidata: String? = null,
        val kinds: String = "",
        val point: Point = Point()
    ) {
        data class Point(
            val lon: Double = 0.0,
            val lat: Double = 0.0
        )
    }

    data class Coordinates(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    // Map the new structure to the expected structure for backwards compatibility
    val attractions: List<Attraction> get() {
        return features.map { feature ->
            Attraction(
                name = feature.name.ifEmpty { "Unnamed Attraction" },
                type = parseKinds(feature.kinds),
                description = "ID: ${feature.xid}",  // No description in the response
                rating = feature.rate.toDouble(),
                address = "",  // No address in the response
                distance = feature.dist,
                image_url = null,
                website = feature.wikidata?.let { "https://www.wikidata.org/wiki/$it" },
                opening_hours = null
            )
        }
    }

    private fun parseKinds(kinds: String): String {
        return kinds.split(",").firstOrNull()?.capitalize() ?: "Attraction"
    }
}

data class Attraction(
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val address: String = "",
    val distance: Double = 0.0,
    val image_url: String? = null,
    val website: String? = null,
    val opening_hours: String? = null
)

// Exchange Rates Model
data class ExchangeRatesResponse(
    val result: String = "",
    val documentation: String = "",
    val terms_of_use: String = "",
    val time_last_update_unix: Long = 0,
    val time_last_update_utc: String = "",
    val time_next_update_unix: Long = 0,
    val time_next_update_utc: String = "",
    val base_code: String = "",
    val conversion_rates: Map<String, Double> = emptyMap(),

    // Backward compatibility properties
    val base_currency: String = "",
    val rates: Map<String, Double> = emptyMap(),
    val updated_at: String = ""
) {
    // Constructor to initialize backward compatibility fields
    constructor(
        result: String = "",
        documentation: String = "",
        terms_of_use: String = "",
        time_last_update_unix: Long = 0,
        time_last_update_utc: String = "",
        time_next_update_unix: Long = 0,
        time_next_update_utc: String = "",
        base_code: String = "",
        conversion_rates: Map<String, Double> = emptyMap()
    ) : this(
        result = result,
        documentation = documentation,
        terms_of_use = terms_of_use,
        time_last_update_unix = time_last_update_unix,
        time_last_update_utc = time_last_update_utc,
        time_next_update_unix = time_next_update_unix,
        time_next_update_utc = time_next_update_utc,
        base_code = base_code,
        conversion_rates = conversion_rates,
        base_currency = base_code,
        rates = conversion_rates,
        updated_at = time_last_update_utc
    )
}

// Chatbot Models
data class ChatbotRequest(
    val message: String
)

data class ChatbotResponse(
    val response: String = "",
    val video_used: String = ""
)

// YouTube Models
data class YouTubeSearchResponse(
    val videos: List<YouTubeVideo> = emptyList()
) {
    data class YouTubeVideo(
        val video_id: String = "",
        val title: String = "",
        val description: String = "",
        val channel_title: String = "",
        val publish_time: String = ""
    ) {
        // Backward compatibility fields
        val id: String get() = video_id
        val thumbnail_url: String get() = "https://i.ytimg.com/vi/$video_id/hqdefault.jpg" // Standard YouTube thumbnail URL
        val channel_name: String get() = channel_title
        val published_at: String get() = formatPublishDate(publish_time)
        val view_count: String get() = "N/A" // Not available in the API response
        val duration: String get() = "N/A" // Not available in the API response
        val url: String get() = "https://www.youtube.com/watch?v=$video_id"

        // Helper function to format the publish date
        private fun formatPublishDate(dateString: String): String {
            return try {
                // Input format: 2025-05-15T08:01:01Z
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString)

                // Output format: May 15, 2025
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                outputFormat.format(date ?: return dateString)
            } catch (e: Exception) {
                dateString // Return original string if parsing fails
            }
        }
    }
}
// Root Response
data class RootResponse(
    val api_name: String = "",
    val version: String = "",
    val status: String = "",
    val documentation_url: String = "",
    val timestamp: String = ""
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
    // Exchange Rates
    suspend fun getExchangeRates(baseCurrency: String = "USD"): Result<ExchangeRatesResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Getting exchange rates for base currency $baseCurrency")
            val response = apiService.getExchangeRates(baseCurrency)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(tag, "Exchange rates retrieved successfully")
                    // Create a properly initialized response with backward compatibility fields
                    val result = ExchangeRatesResponse(
                        result = it.result,
                        documentation = it.documentation,
                        terms_of_use = it.terms_of_use,
                        time_last_update_unix = it.time_last_update_unix,
                        time_last_update_utc = it.time_last_update_utc,
                        time_next_update_unix = it.time_next_update_unix,
                        time_next_update_utc = it.time_next_update_utc,
                        base_code = it.base_code,
                        conversion_rates = it.conversion_rates
                    )
                    Result.success(result)
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
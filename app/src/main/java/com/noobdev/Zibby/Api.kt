package com.noobdev.Zibby

import android.util.Log
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import org.maplibre.geojson.Point
import org.maplibre.geojson.LineString
import com.google.maps.android.PolyUtil

// 1. Define the API interface
interface ORSApiService {
    @GET("v2/directions/driving-car")
    fun getRoute(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<ORSResponse>
}

// 2. Create data models
data class ORSResponse(val routes: List<Route>)
data class Route(val geometry: String)

// 3. Set up Retrofit instance
object ORSClient {
    private const val BASE_URL = "https://api.openrouteservice.org/"
    private const val API_KEY = ""

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ORSApiService by lazy {
        retrofit.create(ORSApiService::class.java)
    }

    // 4. Fetch route and return MapLibre-compatible coordinates
    fun getDirections(
        start: Pair<Double, Double>, // (longitude, latitude)
        end: Pair<Double, Double>,
        onResult: (LineString?) -> Unit // Return a MapLibre LineString
    ) {
        val startString = "${start.first},${start.second}"
        val endString = "${end.first},${end.second}"

        api.getRoute(API_KEY, startString, endString).enqueue(object : Callback<ORSResponse> {
            override fun onResponse(call: Call<ORSResponse>, response: Response<ORSResponse>) {
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()?.geometry
                    val decodedRoute = route?.let { decodePolyline(it) }
                    onResult(decodedRoute)
                } else {
                    Log.e("ORS", "API Error: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ORSResponse>, t: Throwable) {
                Log.e("ORS", "Network Error: ${t.message}")
                onResult(null)
            }
        })
    }

    // 5. Decode polyline and convert to MapLibre LineString
    private fun decodePolyline(encoded: String): LineString {
        val decoded = PolyUtil.decode(encoded)
            .map { Point.fromLngLat(it.longitude, it.latitude) }
        return LineString.fromLngLats(decoded)
    }
}

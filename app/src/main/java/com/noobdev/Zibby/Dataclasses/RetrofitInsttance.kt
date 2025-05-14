package com.noobdev.Zibby.Dataclasses

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Response

object RetrofitInstance {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    val api: ORSApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ORSApi::class.java)
    }
}

interface ORSApi {
    @Headers("Content-Type: application/json")
    @POST("v2/directions/driving-car/geojson")
    suspend fun getRoute(
        @Body body: ORSRequestBody,
        @Query("api_key") apiKey: String
    ): Response<ORSResponse>
}

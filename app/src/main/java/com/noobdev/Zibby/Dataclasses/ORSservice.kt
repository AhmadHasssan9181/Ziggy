package com.noobdev.Zibby.Dataclasses

import com.noobdev.Zibby.ORSResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ORSService {
    @Headers("Content-Type: application/json")
    @POST("v2/directions/driving-car/geojson")
    suspend fun getRoute(
        @Body body: ORSRequestBody,
        @Query("api_key") apiKey: String
    ): Response<ORSResponse>
}

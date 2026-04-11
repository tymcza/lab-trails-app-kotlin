package com.example.myapplication.data.retrofit

import com.example.myapplication.data.types.dto.WarszawaApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiWarszawaService {
    @GET("/api/action/tourism_routes_get/")
    suspend fun getTouristRoutes(
        @Query("apikey") apiKey: String
    ): WarszawaApiResponseDto
}
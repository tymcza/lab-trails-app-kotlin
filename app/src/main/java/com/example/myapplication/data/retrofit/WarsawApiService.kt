package com.example.myapplication.data.retrofit

import com.example.myapplication.data.types.dto.WarsawApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


interface WarsawApiService {
    @GET("/api/action/tourism_routes_get/")
    suspend fun getTouristRoutes(
        @Query("apikey") apiKey: String
    ): WarsawApiResponseDto
}
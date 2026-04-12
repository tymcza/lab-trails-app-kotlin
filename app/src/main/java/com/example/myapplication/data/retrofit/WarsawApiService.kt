package com.example.myapplication.data.retrofit

import retrofit2.http.GET
import retrofit2.http.Query


interface WarsawApiService {
    @GET("/api/action/tourism_routes_get/")
    suspend fun getTouristRoutes(
        @Query("apikey") apiKey: String
    ): WarsawApiResponseDto
}
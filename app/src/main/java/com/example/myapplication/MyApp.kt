package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.room.AppDatabase
import com.example.myapplication.data.DataMediator
import com.example.myapplication.data.retrofit.WarsawApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApp : Application() {

    private val database by lazy {
        AppDatabase.getInstance(this)
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.um.warszawa.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val warsawApiService by lazy {
        retrofit.create(WarsawApiService::class.java)
    }

    val dataMediator by lazy {
        DataMediator(
            database.getRoutesDao(),
            warsawApiService
            ).apply {
            fetchApiRefreshDb()
        }
    }

}
package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.DataMediator

class MyApp : Application() {

    private val database by lazy {
        AppDatabase.getInstance(this)
    }

    val dataMediator by lazy {
        DataMediator(database.getRoutesDao()).apply {
            initializeDataBase()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
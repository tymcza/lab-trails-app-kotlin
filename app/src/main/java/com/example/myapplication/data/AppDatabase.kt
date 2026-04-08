package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.*
import com.example.myapplication.data.entities.*

@Database(entities = [RouteRoom::class, RouteTypeRoom::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRoutesDao(): RoutesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "routes_database"
                ).build().also {INSTANCE = it}
            }
        }
    }
}
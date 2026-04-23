package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.Secrets
import com.example.myapplication.data.RouteRepository.staticRoutes
import com.example.myapplication.data.retrofit.WarsawApiService
import com.example.myapplication.data.room.RoomDao
import com.example.myapplication.data.room.RouteRoom
import com.example.myapplication.data.room.RecordRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class DataMediator(private val dao: RoomDao, private val warsawApiService: WarsawApiService){

    var isReady = false
        private set
    private val _routes = MutableStateFlow(emptyList<RouteCommon>())
    private val _routesCategories = MutableStateFlow(RouteRepository.getRouteTypes())

    fun getDisplayedRoutesFlow(): StateFlow<List<RouteCommon>> { return _routes.asStateFlow() }
    fun getRoutesCategoriesFlow(): StateFlow<List<String>> { return _routesCategories.asStateFlow() }

    private val mediatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initDB() {
        mediatorScope.launch {
            loadStaticToDatabase()
            fetchApi()
            updateRoutesCategories()
        }
        isReady = true
    }
    suspend fun loadStaticToDatabase() {
        try {
            dao.safeUpsertAllRoutes(staticRoutes.map { route -> Mapper.routeCommonToRoom(route) })
            Log.d("MY_LOG", "Static routes data successfully loaded to room DB")
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, loadStaticToDatabase(): ${e.toString()}")
        }
    }

    suspend fun fetchApi() {
        try {
            val warsawApiResponseDto = warsawApiService.getTouristRoutes(apiKey = Secrets.API_KEY)
            val roomEntities = Mapper.routeDtoToRoom(warsawApiResponseDto)
            dao.safeUpsertAllRoutes(roomEntities)
            Log.d("MY_LOG", "Data fetched from Warsaw API successfully loaded to room DB")
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, fetchApiLoadDb(): ${e.toString()}")
        }
    }

    suspend fun getBestRecordById(routeID: String): List<RecordCommon> {
        var record: List<RecordRoom> = emptyList()
        try {
            record = dao.getBestRecordByRouteId(routeID.toLong())
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, getRecordById(${routeID}): ${e.toString()}")
        }
        return record.map { Mapper.recordRoomToCommon(it)}
    }

    suspend fun saveRecord(record: RecordCommon) {
        val recordRoom = Mapper.recordCommonToRoom(record)
        dao.insertRecord(recordRoom)
    }

    suspend fun getRouteById(id: String): RouteCommon? {
        var routeRoom: RouteRoom?
        var routeCommon: RouteCommon? = null
        try {
            routeRoom = dao.getRouteById(id.toLong())
            routeCommon = routeRoom?.let { Mapper.routeRoomToCommon(it) }
        } catch (e: Exception) {
            Log.e("MY_ERR", "Error in data mediator getRouteById(${id}): ${e.toString()}")
        }
        return routeCommon
    }

    suspend fun updateRoutesCategories() {
        try {
            _routesCategories.value = dao.getCategories()
            Log.d("MY_LOG", "Routes categories successfully fetched from roomDB")
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, updateRoutesCategories: ${e.toString()}")
            _routesCategories.value = RouteRepository.getRouteTypes()
        }
    }

    fun updateDisplayedRoutes(category: String) {
        mediatorScope.launch {
            try {
                _routes.value = dao.getRoutesByType(category).map { route -> Mapper.routeRoomToCommon(route) }
                Log.d("MY_LOG", "Routes categories successfully fetched from roomDB")
            } catch (e: Exception) {
                Log.e("MY_ERROR", "Error in DataMediator, updateRoutesCategories: ${e.toString()}")
                _routes.value = RouteRepository.getRoutesByType(category)
            }
        }
    }
}
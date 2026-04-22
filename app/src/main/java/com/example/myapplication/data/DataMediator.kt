package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.Secrets
import com.example.myapplication.data.RouteRepository.staticRoutes
import com.example.myapplication.data.retrofit.WarsawApiService
import com.example.myapplication.data.room.RoutesDao
import com.example.myapplication.data.room.RouteRoom
import com.example.myapplication.data.retrofit.WarsawApiResponseDto
import com.example.myapplication.data.room.RecordRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class DataMediator(private val dao: RoutesDao, private val warsawApiService: WarsawApiService){

    //TODO cleanup the database init logic

    //TODO use full potential of Flow updates - in smart moments

    //TODO delegate translation logic to separate object

    @Volatile
    private var databaseInit = false
    private var databaseRoutes: List<RouteCommon> = listOf()

    private val mediatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    suspend fun refreshDataBase() {
        try{
            val result = dao.getAllRoutes()
            databaseRoutes = result.map { entity -> routeRoomEntityToCommon(entity) }
            databaseInit = true
        } catch (e: Exception) {
            databaseInit = false
            Log.e("MY_ERROR", "Error in DataMediator, loadFromDataBase(): ${e.toString()}")
        }
    }

    suspend fun loadStaticToDatabase() {
        try {
            dao.safeUpsertAllRoutes(staticRoutes.map { route -> routeCommonToRoomEntity(route)})
            Log.d("MY_LOG", "Static routes data successfully loaded to ROOM database")
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, loadStaticToDatabase(): ${e.toString()}")
        }
    }

    fun fetchApiRefreshDb() {
        mediatorScope.launch {
            try {
                val warsawApiResponseDto = warsawApiService.getTouristRoutes(apiKey = Secrets.API_KEY)
                val roomEntities = routeDtoToRoomEntity(warsawApiResponseDto)
                dao.safeUpsertAllRoutes(roomEntities)
                Log.d("MY_LOG", "Data fetched from Warsaw API successfully loaded to ROOM database")
            } catch (e: Exception) {
                Log.e("MY_ERROR", "Error in DataMediator, fetchApiRefreshDb(): ${e.toString()}")
            }
            loadStaticToDatabase()
            refreshDataBase()
        }
    }


    suspend fun getBestRecordById(routeID: String): List<RecordCommon> {
        var record: List<RecordRoom> = emptyList()
        try {
            record = dao.getBestRecordByRouteId(routeID.toLong())
        } catch (e: Exception) {
            Log.e("MY_ERROR", "Error in DataMediator, getRecordById(${routeID}): ${e.toString()}")
        }
        return record.map { recordRoomToRecordCommon(it)}
    }

    fun recordRoomToRecordCommon(record: RecordRoom): RecordCommon {
        val recordCommon = RecordCommon(
            id = record.id.toString(),
            correspondingRouteId = record.correspondingRouteId.toString(),
            registeredTimeSeconds = record.registeredTimeSeconds,
            date = record.date
        )
        return recordCommon
    }

    fun recordCommonToRecordRoom(record: RecordCommon): RecordRoom {
        val recordRoom = RecordRoom(
            id = record.id.toLong(),
            correspondingRouteId = record.correspondingRouteId.toLong(),
            registeredTimeSeconds = record.registeredTimeSeconds,
            date = record.date
        )
        return recordRoom
    }

    suspend fun saveRecord(record: RecordCommon) {
        val recordRoom = recordCommonToRecordRoom(record)
        dao.insertRecord(recordRoom)
    }

    fun getRouteById(id: String): RouteCommon? {
        var routeRoom: RouteRoom?
        var routeCommon: RouteCommon? = null
        mediatorScope.launch {
            try {
                routeRoom = dao.getRouteById(id.toLong())
                routeCommon = routeRoom?.let {routeRoomEntityToCommon(it)}
            } catch (e: Exception) {
                Log.e("MY_ERR", "Error in data mediator getRouteById(${id}): ${e.toString()}")
            }
        }
        return routeCommon
    }

    private val _routesCategories = MutableStateFlow(RouteRepository.getRouteTypes())

    fun fetchRoutesCategories() {
        mediatorScope.launch {
            try {
                _routesCategories.value = dao.getCategories()
                Log.d("MY_LOG", "Routes categories successfully fetched from roomDB")
            } catch (e: Exception) {
                Log.e("MY_ERROR", "Error in DataMediator, fetchRoutesCategories: ${e.toString()}")
                _routesCategories.value = RouteRepository.getRouteTypes()
            }
        }
    }

    fun getRoutesCategoriesFlow(): StateFlow<List<String>> {
        return _routesCategories.asStateFlow()
    }

    private val _routes = MutableStateFlow(emptyList<RouteCommon>())
    fun fetchRoutesByType(category: String) {
        mediatorScope.launch {
            try {
                _routes.value = dao.getRoutesByType(category).map { route -> routeRoomEntityToCommon(route) }
                Log.d("MY_LOG", "Routes categories successfully fetched from roomDB")
            } catch (e: Exception) {
                Log.e("MY_ERROR", "Error in DataMediator, fetchRoutesCategories: ${e.toString()}")
                _routes.value = RouteRepository.getRoutesByType(category)
            }
        }
    }

    fun getRoutesByTypeFlow(): StateFlow<List<RouteCommon>> {
        return _routes.asStateFlow()
    }

    fun routeRoomEntityToCommon(entity: RouteRoom): RouteCommon {
        return RouteCommon(
            entity.id.toString(),
            name = entity.name,
            type = entity.type,
            length = entity.length,
            difficulty = entity.difficulty,
            additionalInfo = entity.additionalInfo
        )
    }

    fun routeCommonToRoomEntity(common: RouteCommon): RouteRoom {
        return RouteRoom(
            id = common.id.toLongOrNull() ?: 0L,
            name = common.name,
            type = common.type,
            length = common.length,
            difficulty = common.difficulty,
            additionalInfo = common.additionalInfo
        )
    }

    fun routeDtoToRoomEntity(response: WarsawApiResponseDto): List<RouteRoom> {
        val routeList = mutableListOf<RouteRoom>()

        for (dto in response.result) {
            val numericLength = dto.length.toDoubleOrNull() ?: 0.0

            val difficultyLabel = when {
                numericLength <= 0.0 -> "No data"
                numericLength < 6.0 -> "Low"
                numericLength < 15.0 -> "Intermediate"
                else -> "High"
            }

            val roomEntity = RouteRoom(
                id = 0,
                name = dto.title,
                type = "Tourist",
                length = dto.length,
                difficulty = difficultyLabel,
                additionalInfo = dto.description
            )

            routeList.add(roomEntity)
        }
        return routeList
    }
}
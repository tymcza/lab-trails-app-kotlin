package com.example.myapplication.data

import com.example.myapplication.data.retrofit.WarsawApiResponseDto
import com.example.myapplication.data.room.RecordRoom
import com.example.myapplication.data.room.RouteRoom

object Mapper {

    fun recordRoomToCommon(record: RecordRoom): RecordCommon {
        val recordCommon = RecordCommon(
            id = record.id.toString(),
            correspondingRouteId = record.correspondingRouteId.toString(),
            registeredTimeSeconds = record.registeredTimeSeconds,
            date = record.date
        )
        return recordCommon
    }

    fun recordCommonToRoom(record: RecordCommon): RecordRoom {
        val recordRoom = RecordRoom(
            id = record.id.toLong(),
            correspondingRouteId = record.correspondingRouteId.toLong(),
            registeredTimeSeconds = record.registeredTimeSeconds,
            date = record.date
        )
        return recordRoom
    }

    fun routeRoomToCommon(entity: RouteRoom): RouteCommon {
        return RouteCommon(
            entity.id.toString(),
            name = entity.name,
            type = entity.type,
            length = entity.length,
            difficulty = entity.difficulty,
            additionalInfo = entity.additionalInfo
        )
    }

    fun routeCommonToRoom(common: RouteCommon): RouteRoom {
        return RouteRoom(
            id = common.id.toLongOrNull() ?: 0L,
            name = common.name,
            type = common.type,
            length = common.length,
            difficulty = common.difficulty,
            additionalInfo = common.additionalInfo
        )
    }

    fun routeDtoToRoom(response: WarsawApiResponseDto): List<RouteRoom> {
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
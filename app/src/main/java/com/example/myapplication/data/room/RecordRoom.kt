package com.example.myapplication.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    indices = [Index(value = ["correspondingRouteId"])],
    foreignKeys = [
        ForeignKey(
            entity = RouteRoom::class,
            parentColumns = ["id"],
            childColumns = ["correspondingRouteId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecordRoom (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correspondingRouteId: Long,
    val registeredTimeSeconds: Long,
    val date: Long
    )
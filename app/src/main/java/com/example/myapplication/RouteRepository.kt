package com.example.myapplication

object RouteRepository {

    val runRoutesNames = listOf("Trasa b1", "Trasa b2", "Trasa b3")
    val bikeRoutesNames = listOf("Trasa r1", "Trasa r2", "Trasa r3")

    val details = mapOf(
        "Trasa b1" to "Dystans: 5km, Trudność: Niska",
        "Trasa b2" to "Dystans: 10km, Trudność: Średnia",
        "Trasa b3" to "Dystans: 15km, Trudność: Wysoka",
        "Trasa r1" to "Dystans: 7km, Trudność: Niska",
        "Trasa r2" to "Dystans: 12km, Trudność: Średnia",
        "Trasa r3" to "Dystans: 20km, Trudność: Wysoka"
    )
}
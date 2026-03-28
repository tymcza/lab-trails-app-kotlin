package com.example.myapplication

import com.example.myapplication.data.RouteRepository

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val runRoutes = RouteRepository.getRoutesByType("Biker")
    private val bikeRoutes = RouteRepository.getRoutesByType("Runner")

    val categories = RouteRepository.getRoutesCategories()
    var displayedRoutesList = mutableStateOf(bikeRoutes)
        private set
    var selectedCategory = mutableStateOf("Biker")
        private set

    fun updateCategory(category: String) {
        when (category) {
            "Runner" -> {
                displayedRoutesList.value = runRoutes
                selectedCategory.value = "Runner"
            }
            "Biker" -> {
                displayedRoutesList.value = bikeRoutes
                selectedCategory.value = "Biker"
            }
            else -> {
                throw IllegalArgumentException("Invalid route type: $category")
            }
        }
    }
}
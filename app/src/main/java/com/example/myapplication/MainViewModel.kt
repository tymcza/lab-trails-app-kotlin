package com.example.myapplication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    val runRoutes = RouteRepository.runRoutesNames
    val bikeRoutes = RouteRepository.bikeRoutesNames

    var displayedNamesList = mutableStateOf(runRoutes)
        private set
    var selectedCategory = mutableStateOf("Biegowe")
        private set

    fun updateCategory(category: String) {
        when (category) {
            "Biegowe" -> {
                displayedNamesList.value = runRoutes
                selectedCategory.value = "Biegowe"
            }
            "Rowerowe" -> {
                displayedNamesList.value = bikeRoutes
                selectedCategory.value = "Rowerowe"
            }
            else -> {
                throw IllegalArgumentException("Invalid route type: $category")
            }
        }
    }
}
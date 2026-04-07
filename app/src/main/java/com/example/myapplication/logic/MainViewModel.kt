package com.example.myapplication.logic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.DataMediator

class MainViewModel(private val mediator: DataMediator) : ViewModel() {

    val categories: List<String>
        get() = mediator.getRoutesCategories()
    var selectedCategory = mutableStateOf(categories.firstOrNull() ?: "Biker")
        private set

    var displayedRoutesList = mutableStateOf(mediator.getRoutesByType(selectedCategory.value))
        private set


    fun updateCategory(category: String) {
        selectedCategory.value = category
        displayedRoutesList.value = mediator.getRoutesByType(selectedCategory.value)
    }
}

class MainViewModelFactory(private val mediator: DataMediator) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(mediator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
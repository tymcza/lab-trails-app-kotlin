package com.example.myapplication.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DataMediator
import kotlinx.coroutines.launch

class MainViewModel(private val mediator: DataMediator) : ViewModel() {

    var selectedCategory: MutableState<String?> = mutableStateOf(null)
        private set
    val categories = mediator.getRoutesCategoriesFlow()
    val displayedRoutesList = mediator.getDisplayedRoutesFlow()

    fun updateCategory(category: String) {
        selectedCategory.value = category
        viewModelScope.launch {
            mediator.updateDisplayedRoutes(category)
            mediator.updateRoutesCategories()
        }
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
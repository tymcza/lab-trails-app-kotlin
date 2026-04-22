package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.DataMediator
import com.example.myapplication.viewmodels.MainViewModel
import com.example.myapplication.viewmodels.MainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediator: DataMediator = (application as MyApp).dataMediator
        val viewModel: MainViewModel by viewModels { MainViewModelFactory(mediator) }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Routes list") }
                        )
                    }
                ) { postScaffoldPadding ->
                    Column(Modifier.padding(postScaffoldPadding)) {
                        DisplayCategories(viewModel = viewModel, Modifier.fillMaxWidth().padding(4.dp))
                        DisplayNamesList(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayCategories(viewModel: MainViewModel, modifier: Modifier = Modifier){
    val categoriesList by viewModel.categories.collectAsState()
    val selectedCategory = viewModel.selectedCategory.value

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categoriesList.forEach { name ->
            if (name == selectedCategory) {
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    border = BorderStroke(2.dp, Color.DarkGray)
                ) {
                    Text(text = name)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.updateCategory(name)
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text(text = name)
                }
            }
        }
    }
}

@Composable
fun DisplayNamesList(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    val routesList = viewModel.displayedRoutesList.value
    val context = LocalContext.current

    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
        items(routesList){
            route ->
            Button(
                onClick = {
                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("routeID", route.id)
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(Color.DarkGray)
            ) {
                Text(
                    text = route.name
                )
            }
        }
    }
}
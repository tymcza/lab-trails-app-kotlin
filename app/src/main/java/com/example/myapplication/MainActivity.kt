package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import kotlin.collections.List
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    val runRoutes = RouteRepository.runRoutesNames
    val bikeRoutes = RouteRepository.bikeRoutesNames
    val categoriesList = listOf("Biegowe", "Rowerowe")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lista tras") }
                        )
                    }
                ) { postScaffoldPadding ->
                    Main(postScaffoldPadding, runRoutes, bikeRoutes, categoriesList)
                }
            }
        }
    }
}


@Composable
fun Main(postScaffoldPadding: PaddingValues, runRoutes: List<String>, bikeRoutes: List<String>, categoriesList: List<String>){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Biegowe"){
        composable("Biegowe") {
            EkranBiegowe(navController = navController, postScaffoldPadding, runRoutes, categoriesList)
        }
        composable("Rowerowe") {
            EkranRowerowe(navController = navController, postScaffoldPadding, bikeRoutes, categoriesList)
        }
    }
}

@Composable
fun EkranBiegowe(navController: NavController, postScaffoldPadding: PaddingValues, runList: List<String>, categoriesList: List<String>){
    Column(modifier = Modifier.padding(postScaffoldPadding)) {
        DisplayCategories(navController = navController, categoriesList, 0, Modifier.fillMaxWidth().padding(4.dp))
        DisplayNamesList(
            runList
        )
    }
}

@Composable
fun EkranRowerowe(navController: NavController, postScaffoldPadding: PaddingValues, bikeRoutes: List<String>, categoriesList: List<String>) {
    Column(modifier = Modifier.padding(postScaffoldPadding)) {
        DisplayCategories(navController = navController, categoriesList, 1, Modifier.fillMaxWidth().padding(4.dp))
        DisplayNamesList(
            bikeRoutes
        )
    }
}

@Composable
fun DisplayCategories(navController: NavController, categoriesList: List<String>, selected: Int, modifier: Modifier = Modifier){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categoriesList.forEachIndexed { index, name ->
            if (index == selected) {
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    border = BorderStroke(2.dp, Color.DarkGray)
                ) {
                    Text(text = name)
                }

            } else {
                Button(
                    onClick = {navController.navigate(name)},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text(text = name)
                }
            }
        }
    }
}

@Composable
fun DisplayNamesList(names: List<String>, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
        items(names){
            name ->
            Button(
                onClick = {
                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("routeName", name)
                    }

                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(Color.DarkGray)
            ) {
                Text(
                    text = name
                )
            }
        }
    }
}
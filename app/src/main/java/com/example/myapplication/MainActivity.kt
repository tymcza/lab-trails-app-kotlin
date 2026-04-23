package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.data.DataMediator
import com.example.myapplication.data.RouteCommon
import com.example.myapplication.viewmodels.MainViewModel
import com.example.myapplication.viewmodels.MainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val mediator: DataMediator = (application as MyApp).dataMediator
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !mediator.isReady }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView

            // Animacja właściwości: Przesunięcie w górę (TranslationY) i zanikanie (Alpha)
            // To symuluje "wyruszenie w trasę"
            val slideUp = ObjectAnimator.ofFloat(
                iconView,
                View.TRANSLATION_Y,
                0f,
                -iconView.height.toFloat() * 2f
            )

            val fadeOut = ObjectAnimator.ofFloat(
                iconView,
                View.ALPHA,
                1f,
                0f
            )

            // Używamy AnimatorSet, aby połączyć te właściwości
            AnimatorSet().apply {
                playTogether(slideUp, fadeOut)
                duration = 800L
                interpolator = AnticipateInterpolator() // Efekt lekkiego cofnięcia przed startem

                // Kluczowe: usunięcie widoku po zakończeniu animacji
                doOnEnd {
                    splashScreenView.remove()
                }
                start()
            }
        }

        super.onCreate(savedInstanceState)


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
fun RouteItem(route: RouteCommon, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Makes the whole card interactable
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${route.length} • ${route.difficulty}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DisplayNamesList(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    val routesList by viewModel.displayedRoutesList.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
        items(routesList){
            route ->
            RouteItem(
                route = route,
                onClick = {
                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("routeID", route.id)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}
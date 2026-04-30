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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.DataMediator
import com.example.myapplication.data.RouteCommon
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodels.DetailsViewModel
import com.example.myapplication.viewmodels.DetailsViewModelFactory
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

            AnimatorSet().apply {
                playTogether(slideUp, fadeOut)
                duration = 800L
                interpolator = AnticipateInterpolator()

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
            val systemInDark = isSystemInDarkTheme()
            val isDarkMode = viewModel.isDarkMode
            LaunchedEffect(Unit) {
                viewModel.initialDarkMode(systemInDark)
            }
            val context = LocalContext.current

            val windowInfo = LocalWindowInfo.current
            val density = LocalDensity.current
            val isTablet = with(density) { windowInfo.containerSize.width.toDp() >= 600.dp }
            var selectedRouteId by remember { mutableStateOf<String?>(null) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                if (isTablet) {
                    Row(Modifier.fillMaxSize()) {
                        Column(Modifier.weight(1f)) {
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        title = {
                                            Text(
                                                text = "Routes list",
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        actions = {
                                            IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                                Icon(
                                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                    contentDescription = "Toggle Theme"
                                                )
                                            }
                                        }
                                    )
                                }
                            ) { padding ->
                                Column(Modifier.padding(padding)) {
                                    DisplayCategories(viewModel = viewModel, Modifier.fillMaxWidth().padding(4.dp))
                                    DisplayNamesList(
                                        viewModel = viewModel,
                                        onRouteClick = { route -> selectedRouteId = route.id }
                                    )
                                }
                            }
                        }

                        Box(Modifier.weight(1.5f).fillMaxHeight()) {
                            if (selectedRouteId != null) {
                                val detailsVm: DetailsViewModel = viewModel(
                                    key = selectedRouteId,
                                    factory = DetailsViewModelFactory(mediator, selectedRouteId!!)
                                )
                                val route = detailsVm.route
                                val bestTime by detailsVm.bestTimeFormatted.collectAsState()
                                val bestDate by detailsVm.bestDateFormatted.collectAsState()

                                if (route != null) {
                                    RouteDetailsScreen(
                                        route = route,
                                        bestTime = bestTime,
                                        bestDate = bestDate,
                                        onBack = { selectedRouteId = null },
                                        viewModel = detailsVm
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        RouteNotFoundScreen(onBack = { selectedRouteId = null })
                                    }
                                }
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Select a route to see details",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Routes list",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                actions = {
                                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Theme"
                                        )
                                    }
                                }
                            )
                        }
                    ) { postScaffoldPadding ->
                        Column(Modifier.padding(postScaffoldPadding)) {
                            DisplayCategories(viewModel = viewModel, Modifier.fillMaxWidth().padding(4.dp))
                            DisplayNamesList(
                                viewModel = viewModel,
                                onRouteClick = { route ->
                                    val intent = Intent(context, DetailsActivity::class.java).apply {
                                        putExtra("routeID", route.id)
                                        putExtra("isDarkMode", isDarkMode)
                                    }
                                    startActivity(intent)
                                }
                            )
                        }
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(text = name)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.updateCategory(name)
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        text = name,
                        color = MaterialTheme.colorScheme.surface
                        )
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${route.length} • ${route.difficulty}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DisplayNamesList(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onRouteClick: (RouteCommon) -> Unit
) {

    val routesList by viewModel.displayedRoutesList.collectAsState()

    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
        items(routesList){
            route ->
            RouteItem(
                route = route,
                onClick = { onRouteClick(route) }
            )
        }
    }
}

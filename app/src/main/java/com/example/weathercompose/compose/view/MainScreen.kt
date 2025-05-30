package com.example.weathercompose.compose.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.weathercompose.AppDestinations
import com.example.weathercompose.compose.theme.primaryDark
import com.example.weathercompose.compose.theme.primaryLight
import com.example.weathercompose.data.network.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(weatherViewModel: WeatherViewModel) {
    var currentScreen by remember { mutableStateOf(AppDestinations.LOCATION_SCREEN_ROUTE) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }

    val topBarTitle = when (currentScreen) {
        AppDestinations.LOCATION_SCREEN_ROUTE -> "Weather"
        AppDestinations.WEATHER_SCREEN_ROUTE -> selectedCity
        else -> "Weather App"
    }

    Scaffold(topBar = {
        when (currentScreen) {
            AppDestinations.SEARCH_SCREEN_ROUTE -> {
                SearchTopAppBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onBack = { currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE })
            }

            else -> {
                val topAppBarColor = when {
                    currentScreen != AppDestinations.WEATHER_SCREEN_ROUTE -> TopAppBarDefaults.topAppBarColors()
                    else -> TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isSystemInDarkTheme()) Color(primaryLight) else Color(primaryDark),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
                TopAppBar(
                    title = { Text(text = topBarTitle, color = Color.White) },
                    colors = topAppBarColor,
                    navigationIcon = {
                        if (currentScreen != AppDestinations.LOCATION_SCREEN_ROUTE) {
                            IconButton(onClick = {
                                currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    tint = Color.White,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        }
    }) { innerPading ->
        Crossfade(targetState = currentScreen, modifier = Modifier.padding(innerPading)) {
            when (it) {
                AppDestinations.LOCATION_SCREEN_ROUTE -> {
                    LocationScreen(
                        weatherViewModel = weatherViewModel,
                        onNavigateToWeatherScreen = { city ->
                            selectedCity = city
                            currentScreen = AppDestinations.WEATHER_SCREEN_ROUTE
                        },
                        onNavigateToSearch = {
                            currentScreen = AppDestinations.SEARCH_SCREEN_ROUTE
                        },
                        onFullyDrawn = { currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE })
                }

                AppDestinations.WEATHER_SCREEN_ROUTE -> {
                    WeatherScreen(
                        weatherViewModel = weatherViewModel,
                        location = selectedCity,
                        onNavigateBack = { currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE },
                        onFullyDrawn = { currentScreen = AppDestinations.WEATHER_SCREEN_ROUTE })
                }

                AppDestinations.SEARCH_SCREEN_ROUTE -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        savedCities = weatherViewModel.savedCitiesStateFlow.collectAsState().value,
                        onNavigateToWeatherScreen = { city ->
                            selectedCity = city
                            currentScreen = AppDestinations.WEATHER_SCREEN_ROUTE
                        },
                        onSaveCityClicked = { cityToSave ->
                            weatherViewModel.saveCity(cityToSave)
                            currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE
                        },
                        onFullyDrawn = { currentScreen = AppDestinations.SEARCH_SCREEN_ROUTE })
                }

            }
        }
//        NavHost(
//            navController = navHostController,
//            startDestination = AppDestinations.LOCATION_SCREEN_ROUTE,
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.background)
//                .padding(innerPading)
//        ) {
//            composable(AppDestinations.LOCATION_SCREEN_ROUTE) {
//                LocationScreen(
//                    weatherViewModel = weatherViewModel,
//                    onNavigateToWeatherScreen = { city ->
//                        selectedCity = city
//                        navHostController.navigate(AppDestinations.WEATHER_SCREEN_ROUTE)
//                    },
//                    onNavigateToSearch = { navHostController.navigate(AppDestinations.SEARCH_SCREEN_ROUTE) },
//                    onFullyDrawn = { currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE })
//            }
//            composable(AppDestinations.WEATHER_SCREEN_ROUTE) {
//                WeatherScreen(
//                    weatherViewModel = weatherViewModel,
//                    location = selectedCity,
//                    onNavigateBack = { navHostController.navigateUp() },
//                    onFullyDrawn = { currentScreen = AppDestinations.WEATHER_SCREEN_ROUTE })
//            }
//            composable(AppDestinations.SEARCH_SCREEN_ROUTE) {
//                SearchScreen(
//                    searchQuery = searchQuery,
//                    savedCities = weatherViewModel.savedCities.collectAsState().value,
//                    onNavigateToWeatherScreen = { city ->
//                        selectedCity = city
//                        navHostController.navigate(AppDestinations.WEATHER_SCREEN_ROUTE)
//                    },
//                    onSaveCityClicked = { cityToSave ->
//                        weatherViewModel.saveCity(cityToSave)
//                        navHostController.navigate(AppDestinations.LOCATION_SCREEN_ROUTE)
//                    },
//                    onFullyDrawn = { currentScreen = AppDestinations.SEARCH_SCREEN_ROUTE })
//            }
    }
}

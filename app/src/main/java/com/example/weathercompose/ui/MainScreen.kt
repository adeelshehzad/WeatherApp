package com.example.weathercompose.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.weathercompose.data.model.WeatherViewModel
import com.example.weathercompose.ui.location.LocationScreen
import com.example.weathercompose.ui.search.SearchScreen
import com.example.weathercompose.ui.search.SearchTopAppBar
import com.example.weathercompose.ui.weather.WeatherScreen
import com.example.weathercompose.utils.AppDestinations

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
                TopAppBar(
                    title = { Text(text = topBarTitle, color = Color.White) },
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
                        })
                }

                AppDestinations.WEATHER_SCREEN_ROUTE -> {
                    WeatherScreen(
                        weatherViewModel = weatherViewModel,
                        location = selectedCity,
                        onNavigateBack = { currentScreen = AppDestinations.LOCATION_SCREEN_ROUTE })
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
    }
}

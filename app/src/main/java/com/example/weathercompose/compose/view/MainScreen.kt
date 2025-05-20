package com.example.weathercompose.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weathercompose.AppDestinations
import com.example.weathercompose.network.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navHostController: NavHostController, weatherViewModel: WeatherViewModel) {
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route
    var searchQuery by remember { mutableStateOf("") }

    val topBarTitle = when (currentDestination) {
        AppDestinations.LOCATION_SCREEN_ROUTE -> "Weather"
        AppDestinations.WEATHER_SCREEN_ROUTE -> "Weather"
        else -> "Weather App"
    }

    Scaffold(topBar = {
        when (currentDestination) {
            AppDestinations.SEARCH_SCREEN_ROUTE -> {
                SearchTopAppBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onBack = { navHostController.navigateUp() })
            }

            else -> {
                TopAppBar(
                    title = { Text(text = topBarTitle) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }) { innerPading ->
        NavHost(
            navController = navHostController,
            startDestination = AppDestinations.LOCATION_SCREEN_ROUTE,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPading)
        ) {
            composable(AppDestinations.LOCATION_SCREEN_ROUTE) {
                LocationScreen(
                    weatherViewModel = weatherViewModel,
                    onNavigateToWeatherScreen = { navHostController.navigate(AppDestinations.WEATHER_SCREEN_ROUTE) },
                    onNavigateToSearch = { navHostController.navigate(AppDestinations.SEARCH_SCREEN_ROUTE) })
            }
            composable(AppDestinations.WEATHER_SCREEN_ROUTE) {
                WeatherScreen(
                    weatherViewModel = weatherViewModel,
                    onNavigateBack = { navHostController.navigateUp() })
            }
            composable(AppDestinations.SEARCH_SCREEN_ROUTE) { SearchScreen() }
        }
    }
}
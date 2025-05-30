package com.example.weathercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.weathercompose.compose.theme.WeatherComposeTheme
import com.example.weathercompose.compose.view.MainScreen
import com.example.weathercompose.data.local.CityDatabaseProvider
import com.example.weathercompose.data.network.RetrofitInstance
import com.example.weathercompose.data.network.WeatherApiRepositoryImpl
import com.example.weathercompose.data.network.WeatherViewModel
import com.example.weathercompose.data.network.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by lazy {
        WeatherViewModelFactory(
            repository = WeatherApiRepositoryImpl(
                apiService = RetrofitInstance.getWeatherApiService(
                    context = this@MainActivity
                ),
                cityDatabase = CityDatabaseProvider.getCityData(this@MainActivity)
            )
        ).create(
            WeatherViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherComposeTheme {
                WeatherAppEntryPoint(
                    weatherViewModel = weatherViewModel,
                )
            }
        }
    }
}

// Define routes as constants for type safety and easy refactoring
object AppDestinations {
    const val LOCATION_SCREEN_ROUTE = "LocationScreen"
    const val WEATHER_SCREEN_ROUTE = "WeatherScreen"
    const val SEARCH_SCREEN_ROUTE = "SearchScreen"
}

@Composable
fun WeatherAppEntryPoint(weatherViewModel: WeatherViewModel) {
    MainScreen(
        weatherViewModel = weatherViewModel
    )
}
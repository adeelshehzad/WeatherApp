package com.example.weathercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.weathercompose.ui.theme.WeatherComposeTheme
import com.example.weathercompose.data.local.CityDatabaseProvider
import com.example.weathercompose.data.network.RetrofitInstance
import com.example.weathercompose.data.model.WeatherApiRepositoryImpl
import com.example.weathercompose.data.model.WeatherViewModel
import com.example.weathercompose.data.model.WeatherViewModelFactory
import com.example.weathercompose.ui.MainScreen

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

@Composable
fun WeatherAppEntryPoint(weatherViewModel: WeatherViewModel) {
    MainScreen(
        weatherViewModel = weatherViewModel
    )
}
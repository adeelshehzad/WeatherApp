package com.example.weathercompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.weathercompose.compose.model.Forecast
import com.example.weathercompose.compose.theme.WeatherComposeTheme
import com.example.weathercompose.compose.view.WeatherUi
import com.example.weathercompose.network.RetrofitInstance
import com.example.weathercompose.network.WeatherApiRepositoryImpl
import com.example.weathercompose.network.WeatherViewModel
import com.example.weathercompose.network.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by lazy {
        WeatherViewModelFactory(WeatherApiRepositoryImpl(RetrofitInstance.api)).create(
            WeatherViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherComposeTheme {
                Scaffold { innerPadding ->
                    WeatherUi(
                        temperature = "25",
                        weatherIcon = R.drawable.ic_sunny,
                        weatherCondition = "Sunny",
                        forecastList = listOf(
                            Forecast("Monday", "25", R.drawable.ic_rain, "Rain"),
                            Forecast("Tuesday", "26", R.drawable.ic_sunny, "Sunny"),
                            Forecast("Wednesday", "27", R.drawable.ic_cloudy, "Cloudy"),
                            Forecast("Thursday", "28", R.drawable.ic_rain, "Rainy"),
                            Forecast("Friday", "29", R.drawable.ic_sunny, "Sunny"),
                            Forecast("Saturday", "30", R.drawable.ic_cloudy, "Cloudy"),
                        ),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        weatherViewModel.getCurrentWeather("L6P4A9")

        weatherViewModel.currentWeather.observe(this) {
            Log.d("WeatherViewModel", "Current weather: $it")
        }
    }
}
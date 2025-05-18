package com.example.weathercompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.weathercompose.compose.model.Forecast
import com.example.weathercompose.compose.theme.WeatherComposeTheme
import com.example.weathercompose.compose.view.WeatherScreen
import com.example.weathercompose.compose.view.WeatherUi
import com.example.weathercompose.model.Weather
import com.example.weathercompose.network.RetrofitInstance
import com.example.weathercompose.network.WeatherApiRepositoryImpl
import com.example.weathercompose.network.WeatherViewModel
import com.example.weathercompose.network.WeatherViewModelFactory
import kotlinx.coroutines.flow.FlowCollector

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
                    WeatherScreen(
                        weatherViewModel = weatherViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
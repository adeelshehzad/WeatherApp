package com.example.weathercompose.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.weathercompose.compose.theme.primaryDark
import com.example.weathercompose.compose.theme.primaryLight
import com.example.weathercompose.data.network.WeatherViewModel
import com.example.weathercompose.model.HourlyData
import com.example.weathercompose.model.WeatherData

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    location: String,
    onNavigateBack: () -> Unit,
    onFullyDrawn: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onFullyDrawn()
    }

    weatherViewModel.getWeather(location)
    val weatherData = weatherViewModel.weatherStateFlow.collectAsState().value

    weatherData?.let {
        WeatherUI(
            weatherData = it,
            modifier = modifier
        )
    }
}

@Composable
fun WeatherUI(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    val color = if (isSystemInDarkTheme()) {
        Color(primaryLight)
    } else {
        Color(primaryDark)
    }
    Column(
        modifier = modifier
            .background(color)
            .fillMaxSize()
    ) {
        Text(
            text = weatherData.weatherCondition,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = weatherData.currentTemperature,
                color = Color.White,
                fontSize = TextUnit(100f, TextUnitType.Sp)
            )
            Icon(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data("https:${weatherData.weatherIcon}").build()
                ),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(96.dp)
            )
        }
        Text(
            text = "Feels like ${weatherData.feelsLike}°C",
            textAlign = TextAlign.Center,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = "High: ${weatherData.highTemperature} Low: ${weatherData.lowTemperature}",
            textAlign = TextAlign.Center,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.DarkGray, MaterialTheme.shapes.medium)
        ) {
            items(weatherData.hourlyData.size) {
                Column(modifier = Modifier
                    .padding(16.dp)) {
                    val hourlyData = weatherData.hourlyData[it]
                    Text(
                        text = hourlyData.time,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${hourlyData.temperature}°C",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data("https:${hourlyData.weatherIcon}").build()
                        ),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = "${hourlyData.changeOfRain}%",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherUiPreview() {
    WeatherUI(
        weatherData = WeatherData(
            currentTemperature = "25",
            weatherCondition = "Sunny",
            locationName = "New York",
            weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
            feelsLike = "24",
            highTemperature = "26",
            lowTemperature = "22",
            hourlyData = listOf(
                HourlyData(
                    time = "12:00",
                    temperature = "25",
                    weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    changeOfRain = 10.0
                ),
                HourlyData(
                    time = "13:00",
                    temperature = "26",
                    weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    changeOfRain = 20.0
                )
            )
        )
    )
}
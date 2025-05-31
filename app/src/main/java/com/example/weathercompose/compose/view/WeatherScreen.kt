package com.example.weathercompose.compose.view

import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weathercompose.R
import com.example.weathercompose.compose.theme.onTertiaryContainerDark
import com.example.weathercompose.compose.theme.onTertiaryContainerLight
import com.example.weathercompose.compose.theme.tertiaryContainerDark
import com.example.weathercompose.compose.theme.tertiaryContainerLight
import com.example.weathercompose.data.network.WeatherViewModel
import com.example.weathercompose.model.HourlyData
import com.example.weathercompose.model.WeatherData

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    location: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    val currentHour = remember {
        Calendar.getInstance().get(
            Calendar.HOUR_OF_DAY
        )
    }
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        listState.animateScrollToItem(currentHour)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = weatherData.weatherCondition,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = weatherData.currentTemperature,
                fontSize = TextUnit(100f, TextUnitType.Sp)
            )
            Image(
                painter = rememberAsyncImagePainter("https:${weatherData.weatherIcon}"),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(96.dp)
                    .aspectRatio(1f)

            )
        }
        Text(
            text = "Feels like ${weatherData.feelsLike}°C",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = "High: ${weatherData.highTemperature} Low: ${weatherData.lowTemperature}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp)) {
                Icon(
                    painter = rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_clock)),
                    contentDescription = "Hourly Forecast",
                )
                Text(
                    text = "Hourly Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            LazyRow(
                state = listState
            ) {
                itemsIndexed(weatherData.hourlyData) { index, hourlyItem ->
                    val isCurrentHour = index == currentHour
                    val backgroundColor =
                        if (isCurrentHour) {
                            if (isSystemInDarkTheme()) {
                                onTertiaryContainerDark
                            } else {
                                onTertiaryContainerLight
                            }
                        } else {
                            Color.Transparent
                        }
                    val textColor = if (isCurrentHour) {
                        if (isSystemInDarkTheme())
                            Color.Black
                        else
                            Color.White
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = hourlyItem.time,
                            textAlign = TextAlign.Center,
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .background(
                                    backgroundColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(4.dp)
                        )

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = "https:${hourlyItem.weatherIcon}"
                            ),
                            contentDescription = "Weather Icon",
                            modifier = Modifier
                                .size(64.dp)
                                .aspectRatio(1f)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = "${hourlyItem.temperature}°C",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Text(
                            text = "${hourlyItem.changeOfRain}%",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
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
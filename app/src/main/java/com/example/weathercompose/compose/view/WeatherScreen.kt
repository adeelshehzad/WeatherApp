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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
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
import com.example.weathercompose.compose.theme.tertiaryDark
import com.example.weathercompose.compose.theme.tertiaryLight
import com.example.weathercompose.data.network.WeatherViewModel
import com.example.weathercompose.model.ForecastData
import com.example.weathercompose.model.HourlyData
import com.example.weathercompose.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Locale

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        HourlyForecast(weatherData = weatherData)
        FutureForecast(weatherData = weatherData)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Precipitation(weatherData = weatherData)
            Wind(weatherData = weatherData)
        }
    }
}

@Composable
fun HourlyForecast(weatherData: WeatherData) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        ) {
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
                    horizontalAlignment = Alignment.CenterHorizontally,
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

@Composable
fun FutureForecast(weatherData: WeatherData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.DateRange),
                contentDescription = "Next 3 Days Forecast"
            )
            Text(
                text = "3 Days Forecast",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            itemsIndexed(weatherData.threeDayForecast) { index, forecastItem ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(
                            color = if (isSystemInDarkTheme()) tertiaryLight else tertiaryDark,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${forecastItem.highTemperature}°C",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${forecastItem.lowTemperature}°C",
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                    )

                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "https:${forecastItem.weatherIcon}"
                        ),
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .size(64.dp)
                            .aspectRatio(1f)
                    )
                    Text(
                        text = forecastItem.chanceOfRain.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = getDayName(forecastItem.date, index == 0),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun Precipitation(weatherData: WeatherData) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(
                color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rain_precip),
                contentDescription = "Precipitation icon",
                tint = Color.Unspecified
            )
            Text(
                text = "Precipitation",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = "${weatherData.precipitation} mm",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total rain for the day",
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_rain),
                contentDescription = "Rain for the day icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun Wind(weatherData: WeatherData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .background(
                color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
                shape = androidx.compose.foundation.shape.CircleShape
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 8.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wind),
                contentDescription = "Wind icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Wind",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = "${weatherData.windSpeed} km/h",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "From ${weatherData.windDirection}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun getDayName(dateStr: String, isCurrentDay: Boolean): String {
    if (isCurrentDay) return "Today"
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateStr) ?: return ""
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return dayFormat.format(date)
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
            precipitation = "0.5",
            windSpeed = "5",
            windDirection = "NNW",
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
            ),
            threeDayForecast = listOf(
                ForecastData(
                    date = "2025-05-31 00:00",
                    highTemperature = "26",
                    lowTemperature = "22",
                    weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    chanceOfRain = 25.0
                ),
                ForecastData(
                    date = "2025-05-31 00:00",
                    highTemperature = "2",
                    lowTemperature = "22",
                    weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    chanceOfRain = 25.0
                ),
                ForecastData(
                    date = "2025-05-31 00:00",
                    highTemperature = "26",
                    lowTemperature = "18",
                    weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    chanceOfRain = 25.0
                )
            )
        )
    )
}
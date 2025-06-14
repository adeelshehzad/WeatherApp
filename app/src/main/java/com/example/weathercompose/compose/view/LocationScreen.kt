package com.example.weathercompose.compose.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.weathercompose.compose.RequestLocationPermission
import com.example.weathercompose.compose.theme.primaryDark
import com.example.weathercompose.compose.theme.primaryLight
import com.example.weathercompose.data.network.WeatherViewModel
import com.example.weathercompose.model.SavedCityWeather
import com.example.weathercompose.model.WeatherData
import com.google.android.gms.location.LocationServices
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun LocationScreen(
    weatherViewModel: WeatherViewModel,
    onNavigateToWeatherScreen: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    weatherViewModel.getSavedCities()
    GetLocationAndWeatherData(context, weatherViewModel)

    val weatherData = weatherViewModel.weatherStateFlow.collectAsState().value
    Log.d("WeatherApp", "weatherData: $weatherData")
    val savedCityWeather = weatherViewModel.savedCitiesStateFlow.collectAsState().value
    Log.d("WeatherApp", "savedCityWeather: $savedCityWeather")

    LocationUi(
        weatherData = weatherData,
        savedCityWeather = savedCityWeather,
        onLocationClick = { onNavigateToWeatherScreen(weatherData?.locationName.orEmpty()) },
        onSearchClick = onNavigateToSearch,
        modifier = modifier
    )
}

@Composable
fun LocationUi(
    weatherData: WeatherData?,
    savedCityWeather: List<SavedCityWeather>,
    onLocationClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchIconBackground = if (isSystemInDarkTheme()) primaryDark else primaryLight
    Column(modifier = modifier.padding(16.dp)) {
        CurrentLocation(weatherData = weatherData, onLocationClick = onLocationClick)
        if (savedCityWeather.isNotEmpty())
            SavedLocations(savedCityWeather = savedCityWeather)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(color = searchIconBackground)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Default.Search),
                contentDescription = "Search Icon",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
fun CurrentLocation(
    weatherData: WeatherData?,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.LocationOn),
            contentDescription = "Current Location"
        )
        Text(
            text = "Current Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    LocationCard(
        weatherData = weatherData,
        onLocationClick = onLocationClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SavedLocations(savedCityWeather: List<SavedCityWeather>, modifier: Modifier = Modifier) {
    Log.d("WeatherApp", "saved location: $savedCityWeather ")
    Row(modifier = modifier.padding(top = 16.dp)) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Star),
            contentDescription = "Saved Location"
        )
        Text(
            text = "Saved Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(savedCityWeather) { city ->
            LocationCard(
                weatherData = city.toWeatherData(),
                onLocationClick = {},
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
fun LocationCard(
    weatherData: WeatherData?,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherIconBackground = if (isSystemInDarkTheme()) Color.White else Color.Black
    Card(
        onClick = onLocationClick, modifier = modifier
            .padding(top = 16.dp),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (weatherData != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "https:${weatherData.weatherIcon}"
                    ),
                    contentDescription = "Location Icon",
                    modifier = Modifier
                        .background(
                            color = weatherIconBackground,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .size(32.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = weatherData.locationName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = weatherData.weatherCondition,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${weatherData.currentTemperature}°C",
                    style = MaterialTheme.typography.headlineSmall,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun GetLocationAndWeatherData(context: Context, weatherViewModel: WeatherViewModel) {
    RequestLocationPermission(
        onPermissionGranted = {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getPostalCode(
                        context = context,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        onResult = { postalCode ->
                            weatherViewModel.getWeather(postalCode)
                        }
                    )
                }
            }
        },
        onPermissionDenied = {

        }
    )
}

fun getPostalCode(
    context: Context,
    latitude: Double,
    longitude: Double,
    onResult: (String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) { p0 -> onResult(p0.firstOrNull()?.postalCode.orEmpty()) }
    } else {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        onResult(addresses?.firstOrNull()?.postalCode.orEmpty())
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LocationUi(
        weatherData = WeatherData(
            currentTemperature = "25",
            weatherCondition = "Sunny",
            locationName = "Brampton",
            weatherIcon = "",
            feelsLike = "25",
            precipitation = "",
            windSpeed = "",
            windDirection = "",
            highTemperature = "25",
            lowTemperature = "12",
            hourlyData = emptyList(),
            threeDayForecast = emptyList()
        ),
        savedCityWeather = listOf(
            SavedCityWeather("Barrie", "25", "Sunny", ""),
            SavedCityWeather("Toronto", "25", "Sunny", "")
        ),
        onLocationClick = {},
        onSearchClick = {}
    )
}
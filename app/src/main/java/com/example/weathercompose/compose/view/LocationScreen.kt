package com.example.weathercompose.compose.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.weathercompose.R
import com.example.weathercompose.compose.RequestLocationPermission
import com.example.weathercompose.compose.theme.primaryDark
import com.example.weathercompose.compose.theme.primaryLight
import com.example.weathercompose.network.WeatherViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun LocationScreen(
    weatherViewModel: WeatherViewModel,
    onNavigateToWeatherScreen: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    GetLocationAndWeatherData(context, weatherViewModel)
    val weatherData = weatherViewModel.currentWeather.collectAsState().value
    if (weatherData != null) {
        LocationUi(
            weatherIconUrl = weatherData.weatherIcon,
            currentLocation = weatherData.locationName,
            weatherCondition = weatherData.weatherCondition,
            currentTemperature = weatherData.currentTemperature,
            onLocationClick = onNavigateToWeatherScreen,
            onSearchClick = onNavigateToSearch,
            modifier = modifier
        )
    }
}

@Composable
fun LocationUi(
    weatherIconUrl: String,
    currentLocation: String,
    weatherCondition: String,
    currentTemperature: String,
    onLocationClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchIconBackground = if (isSystemInDarkTheme()) primaryDark else primaryLight
    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier) {
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
            weatherIconUrl = weatherIconUrl,
            currentLocation = currentLocation,
            weatherCondition = weatherCondition,
            currentTemperature = currentTemperature,
            onLocationClick = onLocationClick
        )

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color = Color(searchIconBackground))
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Default.Search),
                contentDescription = "Search Icon",
                tint = Color.White
            )
        }
    }
}

@Composable
fun LocationCard(
    weatherIconUrl: String,
    currentLocation: String,
    weatherCondition: String,
    currentTemperature: String,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherIconBackground = if (isSystemInDarkTheme()) Color.White else Color.Black
    Card(
        onClick = onLocationClick, modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context = context)
                        .data("https:$weatherIconUrl")
                        .placeholder(R.drawable.ic_airwave)
                        .build()
                ),
                contentDescription = "Location Icon",
                modifier = Modifier
                    .background(
                        color = weatherIconBackground,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .padding(4.dp)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = currentLocation, style = MaterialTheme.typography.titleMedium)
                Text(text = weatherCondition, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$currentTemperature°C",
                style = MaterialTheme.typography.headlineSmall
            )
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
                    val postalCode = getPostalCode(context, location.latitude, location.longitude)
                    weatherViewModel.getCurrentWeather(postalCode)
                }
            }
        },
        onPermissionDenied = {

        }
    )
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LocationUi(
        weatherIconUrl = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
        currentLocation = "Current Location",
        weatherCondition = "Weather Condition",
        currentTemperature = "25",
        onLocationClick = {},
        onSearchClick = {}
    )
}
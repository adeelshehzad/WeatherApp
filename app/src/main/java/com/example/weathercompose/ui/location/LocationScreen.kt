package com.example.weathercompose.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weathercompose.R
import com.example.weathercompose.data.model.SavedCityWeather
import com.example.weathercompose.data.model.UIState
import com.example.weathercompose.data.model.WeatherData
import com.example.weathercompose.data.model.WeatherViewModel
import com.example.weathercompose.ui.theme.primaryDark
import com.example.weathercompose.ui.theme.primaryLight
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
    GetLocationAndWeatherData(
        context = LocalContext.current,
        onPostalCodeResult = { location ->
            weatherViewModel.getWeather(location)
        }
    )
    LaunchedEffect(Unit) {
        weatherViewModel.getSavedCities()
    }

    val uiState by weatherViewModel.uiState.collectAsState()
    val weatherData by weatherViewModel.weatherStateFlow.collectAsState()
    val savedCityWeather by weatherViewModel.savedCitiesStateFlow.collectAsState()

    LocationUi(
        weatherData = weatherData,
        savedCityWeather = savedCityWeather,
        uiState = uiState,
        onLocationClick = onNavigateToWeatherScreen,
        onSearchClick = onNavigateToSearch,
        modifier = modifier
    )
}

@Composable
fun LocationUi(
    weatherData: WeatherData?,
    savedCityWeather: List<SavedCityWeather>,
    uiState: UIState,
    onLocationClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchIconBackground = if (isSystemInDarkTheme()) primaryDark else primaryLight
    Column(modifier = modifier.padding(16.dp)) {
        CurrentLocation(
            weatherData = weatherData,
            uiState = uiState,
            onLocationClick = onLocationClick
        )
        SavedLocations(
            savedCityWeather = savedCityWeather,
            uiState = uiState,
            onLocationClick = onLocationClick
        )
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
                contentDescription = stringResource(R.string.search_icon_icon_cd),
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
fun CurrentLocation(
    weatherData: WeatherData?,
    uiState: UIState,
    onLocationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.LocationOn),
            contentDescription = stringResource(R.string.current_location)
        )
        Text(
            text = stringResource(R.string.current_location),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    LocationCard(
        weatherData = weatherData,
        uiState = uiState,
        onLocationClick = onLocationClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SavedLocations(
    savedCityWeather: List<SavedCityWeather>,
    uiState: UIState,
    onLocationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Row {
            Icon(
                painter = rememberVectorPainter(image = Icons.Default.Star),
                contentDescription = stringResource(R.string.saved_location)
            )
            Text(
                text = stringResource(R.string.saved_location),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        if (uiState is UIState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        } else if (uiState is UIState.Success) {
            if (savedCityWeather.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    items(savedCityWeather) { city ->
                        LocationCard(
                            weatherData = city.toWeatherData(),
                            onLocationClick = onLocationClick,
                            uiState = uiState,
                            modifier = Modifier.fillParentMaxWidth()
                        )
                    }
                }
            } else {
                EmptyView()
            }
        } else if (uiState is UIState.Error) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_error),
                    contentDescription = stringResource(R.string.error),
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(R.string.error_fetching_saved_locations),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun LocationCard(
    weatherData: WeatherData?,
    onLocationClick: (String) -> Unit,
    uiState: UIState,
    modifier: Modifier = Modifier
) {
    val weatherIconBackground = if (isSystemInDarkTheme()) Color.White else Color.Black
    Card(
        onClick = { onLocationClick(weatherData?.locationName.orEmpty()) },
        modifier = modifier.padding(top = 16.dp),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState is UIState.Success) {
                if (weatherData == null) {
                    ErrorView(weatherIconBackground = weatherIconBackground)
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "https:${weatherData.weatherIcon}"
                        ),
                        contentDescription = stringResource(R.string.location_icon),
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
                        text = stringResource(
                            R.string.degree_centigrade,
                            weatherData.currentTemperature
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            } else if (uiState is UIState.Loading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
            } else if (uiState is UIState.Error) {
                ErrorView(weatherIconBackground = weatherIconBackground)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun GetLocationAndWeatherData(context: Context, onPostalCodeResult: (String) -> Unit) {
    RequestLocationPermission(
        onPermissionGranted = {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getPostalCode(
                        context = context,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        onPostalCodeResult = onPostalCodeResult
                    )
                }
            }
        },
        onPermissionDenied = {

        }
    )
}

@Suppress("DEPRECATION")
fun getPostalCode(
    context: Context,
    latitude: Double,
    longitude: Double,
    onPostalCodeResult: (String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) { p0 -> onPostalCodeResult(p0.firstOrNull()?.postalCode.orEmpty()) }
    } else {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        onPostalCodeResult(addresses?.firstOrNull()?.postalCode.orEmpty())
    }
}

@Composable
fun EmptyView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_empty_record),
            contentDescription = stringResource(R.string.no_saved_locations),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(R.string.no_saved_locations),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun ErrorView(weatherIconBackground: Color) {
    Icon(
        painter = painterResource(R.drawable.ic_error),
        contentDescription = stringResource(R.string.error),
        tint = Color.Red,
        modifier = Modifier
            .background(
                color = weatherIconBackground,
                shape = CircleShape
            )
            .padding(4.dp)
            .size(32.dp)
    )
    Text(
        text = stringResource(R.string.error_fetching_location_weather),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 8.dp)
    )
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
            windDegree = 0,
            highTemperature = "25",
            lowTemperature = "12",
            sunrise = "",
            sunset = "",
            hourlyData = emptyList(),
            threeDayForecast = emptyList()
        ),
        savedCityWeather = listOf(
            SavedCityWeather("Barrie", "25", "Sunny", ""),
            SavedCityWeather("Toronto", "25", "Sunny", "")
        ),
//        weatherData = null,
//        savedCityWeather = emptyList(),
        uiState = UIState.Success,
        onLocationClick = {},
        onSearchClick = {}
    )
}
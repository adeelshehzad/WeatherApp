package com.example.weathercompose.compose.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weathercompose.R
import com.example.weathercompose.compose.model.Forecast
import com.example.weathercompose.compose.theme.WeatherComposeTheme
import com.example.weathercompose.model.Weather
import com.example.weathercompose.model.WeatherData
import com.example.weathercompose.network.WeatherViewModel

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    var locationInput by remember { mutableStateOf("") }

    WeatherUi(
        weatherData= weatherViewModel.currentWeather.collectAsState().value,
        locationInput = locationInput,
        onLocationValueChange = { locationInput = it },
        onGetWeatherBtnClick = { weatherViewModel.getCurrentWeather(locationInput) },
        modifier = modifier
    )
}

@Composable
fun WeatherUi(
    weatherData: WeatherData?,
    locationInput: String,
    onLocationValueChange: (String) -> Unit,
    onGetWeatherBtnClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextField(
            value = locationInput,
            onValueChange = onLocationValueChange,
            label = { Text("Enter postal code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        OutlinedButton(
            onClick = onGetWeatherBtnClick, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Update")
        }
        if (weatherData != null) {
            Text(
                text = weatherData.locationName,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${weatherData.currentTemperature}°C",
                    style = MaterialTheme.typography.headlineLarge,
                )

                Image(
                    bitmap = weatherData.weatherIcon?.asImageBitmap() ?: ImageBitmap(1, 1),
                    contentDescription = "",
                    modifier = Modifier.size(size = 64.dp)
                )
            }
            Text(
                text = weatherData.weatherCondition,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
//        Text(
//            text = "Forecast",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 32.dp)
//        )
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            modifier = Modifier.padding(top = 16.dp)
//        ) {
//            items(forecastList.size) { forecast ->
//                Card {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(text = forecastList[forecast].day)
//                        Icon(
//                            painter = painterResource(forecastList[forecast].weatherIcon),
//                            contentDescription = "",
//                            modifier = Modifier.size(width = 32.dp, height = 32.dp),
//                            tint = Color.Unspecified
//                        )
//                        Text(text = forecastList[forecast].temperature)
//                        Text(text = forecastList[forecast].weatherCondition)
//                    }
//                }
//            }
//        }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherUiPreview() {
    WeatherComposeTheme {
        val context = LocalContext.current
        Scaffold { innerPadding ->
            WeatherUi(
                WeatherData(
                    currentTemperature = "25",
                    locationName = "Toronto",
                    weatherCondition = "Sunny",
                    weatherIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_sunny)
                ),
                locationInput = "",
                onLocationValueChange = {},
                onGetWeatherBtnClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
package com.example.weathercompose.compose.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weathercompose.R
import com.example.weathercompose.compose.model.Forecast
import com.example.weathercompose.compose.theme.WeatherComposeTheme

@Composable
fun WeatherUi(
    temperature: String,
    weatherIcon: Int,
    weatherCondition: String,
    forecastList: List<Forecast>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Toronto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$temperature°C",
                style = MaterialTheme.typography.headlineLarge,
            )

            Icon(
                painter = painterResource(weatherIcon),
                contentDescription = "",
                modifier = Modifier.size(width = 64.dp, height = 64.dp),
                tint = Color.Unspecified
            )
        }
        Text(
            text = weatherCondition,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Forecast",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(forecastList.size) { forecast ->
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = forecastList[forecast].day)
                        Icon(
                            painter = painterResource(forecastList[forecast].weatherIcon),
                            contentDescription = "",
                            modifier = Modifier.size(width = 32.dp, height = 32.dp),
                            tint = Color.Unspecified
                        )
                        Text(text = forecastList[forecast].temperature)
                        Text(text = forecastList[forecast].weatherCondition)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherUiPreview() {
    WeatherComposeTheme {
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
            )
        )
    }
}
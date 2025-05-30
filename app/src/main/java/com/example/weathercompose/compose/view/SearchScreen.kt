package com.example.weathercompose.compose.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weathercompose.loadCityListFromAssets
import com.example.weathercompose.model.SavedCityWeather

@Composable
fun SearchScreen(
    searchQuery: String,
    savedCities: List<SavedCityWeather>,
    onNavigateToWeatherScreen: (String) -> Unit,
    onSaveCityClicked: (String) -> Unit,
    onFullyDrawn: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onFullyDrawn()
    }

    HorizontalDivider()

    val context = LocalContext.current
    val allCities by remember { mutableStateOf(loadCityListFromAssets(context)) }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            allCities.filter {
                it.startsWith(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(modifier = modifier) {
        items(filteredCities) { cities ->
            val isCityAlreadySaved = remember(savedCities, cities) {
                savedCities.any { savedCity ->
                    savedCity.cityName.equals(
                        other = cities,
                        ignoreCase = true
                    )
                }
            }

            Row {
                Text(
                    text = cities,
                    modifier = Modifier
                        .clickable {
                            onNavigateToWeatherScreen(cities)
                        }
                        .padding(all = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .width(80.dp) // Match width of the Button
                        .height(36.dp), // Match height of the Button
                    contentAlignment = Alignment.Center
                ) {
                    if (!isCityAlreadySaved) {
                        Button(
                            onClick = { onSaveCityClicked(cities) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }
}
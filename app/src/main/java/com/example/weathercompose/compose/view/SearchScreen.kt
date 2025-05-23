package com.example.weathercompose.compose.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weathercompose.loadCityListFromAssets
import com.example.weathercompose.model.SavedCityWeather

@Composable
fun SearchScreen(
    searchQuery: String,
    savedCities: List<SavedCityWeather>,
    onCitySelected: (String) -> Unit,
    onNavigateToWeatherScreen: () -> Unit,
    onSaveCityClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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

    Column(modifier = modifier) {
        LazyColumn {
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
                                onCitySelected(cities)
                                onNavigateToWeatherScreen()
                            }
                            .padding(all = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (!isCityAlreadySaved) {
                        Button(onClick = { onSaveCityClicked(cities) }) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }
}
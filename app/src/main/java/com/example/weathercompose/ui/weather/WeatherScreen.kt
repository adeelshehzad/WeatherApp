package com.example.weathercompose.ui.weather

import android.icu.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weathercompose.R
import com.example.weathercompose.ui.theme.onSecondaryDark
import com.example.weathercompose.ui.theme.onSecondaryLight
import com.example.weathercompose.ui.theme.onTertiaryContainerDark
import com.example.weathercompose.ui.theme.onTertiaryContainerLight
import com.example.weathercompose.ui.theme.tertiaryContainerDark
import com.example.weathercompose.ui.theme.tertiaryContainerLight
import com.example.weathercompose.ui.theme.tertiaryDark
import com.example.weathercompose.ui.theme.tertiaryLight
import com.example.weathercompose.data.model.WeatherViewModel
import com.example.weathercompose.data.model.ForecastData
import com.example.weathercompose.data.model.HourlyData
import com.example.weathercompose.data.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

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

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SunriseSunsetCard(sunriseTime = weatherData.sunrise, sunsetTime = weatherData.sunset)
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
            .size(200.dp)
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
    val arrowShapeRotation by remember { mutableFloatStateOf(getRotationByWindDegree(weatherData.windDegree)) }
    val windDirection by remember { mutableStateOf(getWindDirection(weatherData.windDegree)) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.background(
            color = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight,
            shape = CircleShape
        )
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    rotationZ = arrowShapeRotation
                }
                .clip(WindDirectionArrowShape())
                .background(if (isSystemInDarkTheme()) onSecondaryDark else onSecondaryLight)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
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
                text = "From $windDirection",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun getRotationByWindDegree(windDegree: Int): Float {
    return (((windDegree % 360) / 45.0).roundToInt() % 8) * 45f
}

private fun getWindDirection(windDegree: Int): String {
    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val index = ((windDegree % 360) / 45.0).roundToInt() % 8
    return directions[index]
}

private fun getDayName(dateStr: String, isCurrentDay: Boolean): String {
    if (isCurrentDay) return "Today"
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateStr) ?: return ""
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return dayFormat.format(date)
}

@Preview(showBackground = true, device = "id:pixel_9_pro")
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
            windDegree = 225,
            highTemperature = "26",
            lowTemperature = "22",
            sunrise = "5:37 a.m.",
            sunset = "8:58 p.m.",
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

@Composable
fun SunriseSunsetCard(
    sunriseTime: String,
    sunsetTime: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) tertiaryContainerDark else tertiaryContainerLight),
        modifier = Modifier
            .padding(16.dp)
            .size(220.dp)
    ) {
        Column {
            // Header row
            Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                Icon(
                    imageVector = Icons.Default.Star, // or sunrise icon
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sunrise & sunset", style = MaterialTheme.typography.titleMedium)
            }

            // Sun path
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(150.dp)
            ) {
                SunPathCurve(
                    curveColor = Color(0xFF97BAFC),
                    progress = 0.95f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                DayNightDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                // Times
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star, // sunrise icon
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sunriseTime,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.LightGray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star, // sunset icon
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sunsetTime,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SunPathCurve(
    curveColor: Color,
    progress: Float,
    modifier: Modifier = Modifier,
    sunColor: Color = Color(0xFFFFA500)
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val arcMid = width * 0.5f
        val baseY = height * 0.5f
        val peakY = height * 0.05f

        val leftStart = Offset(0f, baseY)
        val leftCp1 = Offset(arcMid * 0.5f, baseY)
        val leftCp2 = Offset(arcMid - arcMid * 0.5f, peakY)
        val arcPeak = Offset(arcMid, peakY)
        val rightCp1 = Offset(arcMid + arcMid * 0.5f, peakY)
        val rightCp2 = Offset(width - (width - arcMid) * 0.5f, baseY)
        val rightEnd = Offset(width, baseY)

        val path = Path().apply {
            moveTo(leftStart.x, leftStart.y)
            cubicTo(leftCp1.x, leftCp1.y, leftCp2.x, leftCp2.y, arcPeak.x, arcPeak.y)
            cubicTo(rightCp1.x, rightCp1.y, rightCp2.x, rightCp2.y, rightEnd.x, rightEnd.y)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        fun cubicBezier(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
            val oneMinusT = 1 - t
            val x = oneMinusT.pow(3) * p0.x +
                    3 * oneMinusT.pow(2) * t * p1.x +
                    3 * oneMinusT * t.pow(2) * p2.x +
                    t.pow(3) * p3.x
            val y = oneMinusT.pow(3) * p0.y +
                    3 * oneMinusT.pow(2) * t * p1.y +
                    3 * oneMinusT * t.pow(2) * p2.y +
                    t.pow(3) * p3.y
            return Offset(x, y)
        }

        val sunPos = if (progress <= 0.5f) {
            val t = progress * 2f
            cubicBezier(leftStart, leftCp1, leftCp2, arcPeak, t)
        } else {
            val t = (progress - 0.5f) * 2f
            cubicBezier(arcPeak, rightCp1, rightCp2, rightEnd, t)
        }

        drawPath(path, color = curveColor)
        drawCircle(color = sunColor, radius = 10.dp.toPx(), center = sunPos)
    }
}


@Composable
fun DayNightDivider(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val baseY = height * 0.5f

        val shadedAreaPath = Path().apply {
            moveTo(0f, baseY)
            lineTo(0f, baseY)
            lineTo(width, baseY)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(shadedAreaPath, color = Color(0x55000000))
    }
}


class WindDirectionArrowShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.45f, size.height * 0.92f)
            // Left edge curve up
            cubicTo(
                size.width * 0.25f, size.height * 0.70f,
                size.width * 0.15f, size.height * 0.45f,
                size.width * 0.15f, size.height * 0.35f
            )
            // Left arm horizontal out, then curve into notch
            cubicTo(
                size.width * 0.15f, size.height * 0.20f,
                size.width * 0.35f, size.height * 0.12f,
                size.width * 0.50f, size.height * 0.25f
            )
            // Right arm out, then down
            cubicTo(
                size.width * 0.65f, size.height * 0.10f,
                size.width * 0.85f, size.height * 0.20f,
                size.width * 0.84f, size.height * 0.35f
            )
            // Right edge curve down
            cubicTo(
                size.width * 0.85f, size.height * 0.45f,
                size.width * 0.75f, size.height * 0.70f,
                size.width * 0.55f, size.height * 0.92f
            )
            // Rounded bottom curve
            cubicTo(
                size.width * 0.50f, size.height * 0.94f, // control point 1
                size.width * 0.50f, size.height * 0.94f, // control point 2
                size.width * 0.45f, size.height * 0.92f  // back to start
            )

            close()
        }
        return Outline.Generic(path)
    }
}

package com.balu.weatherapp.ui.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.balu.weatherapp.data.model.WeatherResponse
import com.balu.weatherapp.viewstate.WeatherUiState
import androidx.compose.ui.tooling.preview.Preview
import com.balu.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onSearchAction: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        WeatherContent(state = state, onSearchAction = onSearchAction)
    }
}

@Composable
fun WeatherContent(state: WeatherUiState, onSearchAction: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherSearchBar(onSearchAction = onSearchAction)
        Spacer(modifier = Modifier.height(24.dp))
        when (state) {
            is WeatherUiState.Loading -> LoadingView()
            is WeatherUiState.Success -> WeatherDetailsCard(data = state.data)
            is WeatherUiState.Error -> ErrorView(message = state.message)
            is WeatherUiState.Empty -> EmptyStateView()
        }
    }
}

@Composable
private fun WeatherSearchBar(onSearchAction: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = { if(searchText.isNotBlank()) onSearchAction(searchText) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Check Weather")
        }

    }
}

@Preview
@Composable
private fun WeatherSearchBarPreview() {
    WeatherAppTheme {
        WeatherSearchBar(onSearchAction = {})
    }
}

@Composable
private fun WeatherDetailsCard(data: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.city ?: "Unknown Location",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Image Caching via Coil
            val iconCode = data.weather?.firstOrNull()?.icon
            AsyncImage(
                model = "https://openweathermap.org/img/wn/$iconCode@4x.png",
                contentDescription = "Weather Icon",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "${data.main?.temp?.toInt() ?: "--"}°F",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light
            )

            Text(
                text = data.weather?.firstOrNull()?.description?.uppercase() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            WeatherInfoRow(humidity = data.main?.humidity?.toInt() ?: 0)
        }
    }
}

@Preview
@Composable
private fun WeatherDetailsCardPreview() {
    // Mocking the Java WeatherResponse model
    val mockResponse = WeatherResponse().apply {
        city = "Chicago"
        main = WeatherResponse.Main().apply {
            temp = 72.5
            humidity = 45.0
        }
        weather = listOf(WeatherResponse.Weather().apply {
            description = "clear sky"
            icon = "01d"
        })
    }
    WeatherDetailsCard(mockResponse)
}

@Composable
private fun WeatherInfoRow(humidity: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("HUMIDITY", style = MaterialTheme.typography.labelSmall)
            Text("$humidity%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    WeatherAppTheme {
        LoadingView()
    }
}


@Composable
private fun ErrorView(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview
@Composable
private fun ErrorPreview() {
    WeatherAppTheme {
        ErrorView(message = "Unable to connect to weather service. Please check your internet.")
    }
}

@Composable
private fun EmptyStateView() {
    Text(
        text = "Please enter a US city to view current weather conditions.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun PreviewEmptyState() {
    WeatherAppTheme {
        WeatherScreen(
            state = WeatherUiState.Empty,
            onSearchAction = {}
        )
    }
}
@Preview(showBackground = true, name = "Loading State")
@Composable
fun PreviewLoadingState() {
    WeatherAppTheme {
        WeatherScreen(
            state = WeatherUiState.Loading,
            onSearchAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun PreviewSuccessState() {
    // Mocking the Java WeatherResponse model
    val mockResponse = WeatherResponse().apply {
        city = "Chicago"
        main = WeatherResponse.Main().apply {
            temp = 72.5
            humidity = 45.0
        }
        weather = listOf(WeatherResponse.Weather().apply {
            description = "clear sky"
            icon = "01d"
        })
    }

    WeatherAppTheme {
        WeatherScreen(
            state = WeatherUiState.Success(mockResponse),
            onSearchAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun PreviewErrorState() {
    WeatherAppTheme {
        WeatherScreen(
            state = WeatherUiState.Error("Unable to connect to weather service. Please check your internet."),
            onSearchAction = {}
        )
    }
}
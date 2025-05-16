package com.noobdev.Zibby.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noobdev.Zibby.geminiApi.AttractionResponse
import com.noobdev.Zibby.geminiApi.BudgetResponse
import com.noobdev.Zibby.geminiApi.ChatbotResponse
import com.noobdev.Zibby.geminiApi.DestinationResponse
import com.noobdev.Zibby.geminiApi.ExchangeRatesResponse
import com.noobdev.Zibby.geminiApi.HotelSearchResponse
import com.noobdev.Zibby.geminiApi.TravelAdviceResponse
import com.noobdev.Zibby.geminiApi.TravelRepository
import com.noobdev.Zibby.geminiApi.TripPlanResponse
import com.noobdev.Zibby.geminiApi.WeatherResponse
import com.noobdev.Zibby.geminiApi.YouTubeSearchResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TravelPlannerTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TravelPlannerTestApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlannerTestApp() {
    val navController = rememberNavController()
    val viewModel: TravelPlannerViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Planner API Tester") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("Budget") },
                    label = { Text("Budget") },
                    selected = currentRoute == "budget",
                    onClick = { navController.navigate("budget") }
                )
                NavigationBarItem(
                    icon = { Text("Trip") },
                    label = { Text("Trip") },
                    selected = currentRoute == "trip",
                    onClick = { navController.navigate("trip") }
                )
                NavigationBarItem(
                    icon = { Text("Hotels") },
                    label = { Text("Hotels") },
                    selected = currentRoute == "hotels",
                    onClick = { navController.navigate("hotels") }
                )
                NavigationBarItem(
                    icon = { Text("More") },
                    label = { Text("More") },
                    selected = currentRoute == "more",
                    onClick = { navController.navigate("more") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "budget",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("budget") {
                BudgetScreen(viewModel)
            }
            composable("trip") {
                TripPlanScreen(viewModel)
            }
            composable("hotels") {
                HotelSearchScreen(viewModel)
            }
            composable("more") {
                MoreOptionsScreen(navController)
            }
            composable("advice") {
                TravelAdviceScreen(viewModel)
            }
            composable("destination") {
                DestinationScreen(viewModel)
            }
            composable("weather") {
                WeatherScreen(viewModel)
            }
            composable("attractions") {
                AttractionsScreen(viewModel)
            }
            composable("exchange") {
                ExchangeRatesScreen(viewModel)
            }
            composable("youtube") {
                YouTubeScreen(viewModel)
            }
            composable("chatbot") {
                ChatbotScreen(viewModel)
            }
        }
    }
}
// Budget Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(viewModel: TravelPlannerViewModel) {
    var destination by remember { mutableStateOf("New York") }
    var durationDays by remember { mutableStateOf("5") }
    var travelers by remember { mutableStateOf("2") }
    var budgetLevel by remember { mutableStateOf("mid-range") }
    var includeFlights by remember { mutableStateOf(true) }
    var includeAccommodation by remember { mutableStateOf(true) }
    var includeActivities by remember { mutableStateOf(true) }
    var includeFood by remember { mutableStateOf(true) }
    var origin by remember { mutableStateOf("Los Angeles") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Budget Generator",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = durationDays,
            onValueChange = { durationDays = it },
            label = { Text("Duration (days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = travelers,
            onValueChange = { travelers = it },
            label = { Text("Number of Travelers") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Budget level dropdown
        var expanded by remember { mutableStateOf(false) }
        val budgetOptions = listOf("economy", "mid-range", "luxury")

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = budgetLevel,
                onValueChange = { },
                label = { Text("Budget Level") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, "dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                budgetOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            budgetLevel = option
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Origin (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Checkboxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeFlights,
                onCheckedChange = { includeFlights = it }
            )
            Text("Include Flights")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeAccommodation,
                onCheckedChange = { includeAccommodation = it }
            )
            Text("Include Accommodation")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeActivities,
                onCheckedChange = { includeActivities = it }
            )
            Text("Include Activities")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeFood,
                onCheckedChange = { includeFood = it }
            )
            Text("Include Food")
        }

        Button(
            onClick = {
                viewModel.generateBudget(
                    destination = destination,
                    durationDays = durationDays.toIntOrNull() ?: 1,
                    travelers = travelers.toIntOrNull() ?: 1,
                    budgetLevel = budgetLevel,
                    includeFlights = includeFlights,
                    includeAccommodation = includeAccommodation,
                    includeActivities = includeActivities,
                    includeFood = includeFood,
                    origin = if (origin.isBlank()) null else origin
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Generate Budget", color = Color.White)
        }

        // Show results
        viewModel.budgetResult.value?.let { budget ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Total Budget: ${budget.total_budget} ${budget.currency}",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Breakdown:", fontWeight = FontWeight.Bold)

                    if (budget.breakdown != null && budget.breakdown.isNotEmpty()) {
                        budget.breakdown.forEach { (category, amount) ->
                            Text("• $category: $amount ${budget.currency}")
                        }
                    } else {
                        Text("No breakdown available")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tips:", fontWeight = FontWeight.Bold)

                    if (budget.tips != null && budget.tips.isNotEmpty()) {
                        budget.tips.forEach { tip ->
                            Text("• $tip")
                        }
                    } else {
                        Text("No tips available")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// Trip Plan Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlanScreen(viewModel: TravelPlannerViewModel) {
    var destination by remember { mutableStateOf("Paris") }
    var durationDays by remember { mutableStateOf("7") }
    var interestsText by remember { mutableStateOf("food, culture, history") }
    var travelStyle by remember { mutableStateOf("cultural") }
    var travelDates by remember { mutableStateOf(getCurrentDateString()) }
    var includeAccommodation by remember { mutableStateOf(true) }
    var includeTransportation by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Trip Planner",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = durationDays,
            onValueChange = { durationDays = it },
            label = { Text("Duration (days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = interestsText,
            onValueChange = { interestsText = it },
            label = { Text("Interests (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Travel style dropdown
        var expanded by remember { mutableStateOf(false) }
        val styleOptions = listOf("relaxed", "adventurous", "cultural", "luxury", "budget")

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = travelStyle,
                onValueChange = { },
                label = { Text("Travel Style") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, "dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                styleOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            travelStyle = option
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = travelDates,
            onValueChange = { travelDates = it },
            label = { Text("Travel Dates (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Checkboxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeAccommodation,
                onCheckedChange = { includeAccommodation = it }
            )
            Text("Include Accommodation")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeTransportation,
                onCheckedChange = { includeTransportation = it }
            )
            Text("Include Transportation")
        }

        Button(
            onClick = {
                val interests = interestsText.split(",").map { it.trim() }
                viewModel.generateTripPlan(
                    destination = destination,
                    durationDays = durationDays.toIntOrNull() ?: 1,
                    interests = interests,
                    travelStyle = travelStyle,
                    travelDates = travelDates.takeIf { it.isNotBlank() },
                    includeAccommodation = includeAccommodation,
                    includeTransportation = includeTransportation
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Generate Trip Plan", color = Color.White)
        }

        // Show results
        viewModel.tripPlanResult.value?.let { tripPlan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Destination Overview",
                        fontWeight = FontWeight.Bold
                    )

                    Text(tripPlan.destination_overview)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Daily Itinerary",
                        fontWeight = FontWeight.Bold
                    )

                    if (tripPlan.daily_itinerary.isNotEmpty()) {
                        tripPlan.daily_itinerary.forEach { day ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Day ${day.day}", fontWeight = FontWeight.Bold)

                            if (day.activities.isNotEmpty()) {
                                day.activities.forEach { activity ->
                                    Text("• ${activity.time}: ${activity.name}")
                                    Text("  ${activity.description}")
                                }
                            } else {
                                Text("  No activities planned for this day")
                            }
                        }
                    } else {
                        Text("No daily itinerary available")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Transportation Tips",
                        fontWeight = FontWeight.Bold
                    )

                    if (tripPlan.transportation_tips.isNotEmpty()) {
                        tripPlan.transportation_tips.forEach { tip ->
                            Text("• $tip")
                        }
                    } else {
                        Text("No transportation tips available")
                    }

                    if (tripPlan.accommodation_suggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Accommodation Suggestions",
                            fontWeight = FontWeight.Bold
                        )

                        tripPlan.accommodation_suggestions.forEach { accommodation ->
                            Text("• ${accommodation.name} (${accommodation.type})")
                            Text("  Price Range: ${accommodation.price_range}")
                            accommodation.description?.let { Text("  $it") }
                        }
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// Hotel Search Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreen(viewModel: TravelPlannerViewModel) {
    var location by remember { mutableStateOf("London") }
    var checkInDate by remember { mutableStateOf(getCurrentDateString()) }
    var checkOutDate by remember { mutableStateOf(getDateAfterDays(7)) }
    var guests by remember { mutableStateOf("2") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Hotel Search",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = checkInDate,
            onValueChange = { checkInDate = it },
            label = { Text("Check-in Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = checkOutDate,
            onValueChange = { checkOutDate = it },
            label = { Text("Check-out Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = guests,
            onValueChange = { guests = it },
            label = { Text("Guests") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.searchHotels(
                    location = location,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    guests = guests.toIntOrNull() ?: 2
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Search Hotels", color = Color.White)
        }

        // Show results
        viewModel.hotelSearchResult.value?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Search Results for ${result.search_info.location}",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "${result.search_info.check_in} to ${result.search_info.check_out}, ${result.search_info.guests} guests"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Price Ranges:", fontWeight = FontWeight.Bold)
                    Text("• Budget: ${result.price_range.budget}")
                    Text("• Mid-range: ${result.price_range.mid_range}")
                    Text("• Luxury: ${result.price_range.luxury}")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Hotels Found: ${result.hotels.size}", fontWeight = FontWeight.Bold)

                    if (result.hotels.isNotEmpty()) {
                        result.hotels.forEach { hotel ->
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(hotel.name, fontWeight = FontWeight.Bold)
                            Text("Rating: ${hotel.rating}/5.0")
                            Text("Price: ${hotel.price}")
                            Text("Address: ${hotel.address}")
                            Text(hotel.description)

                            Text("Amenities:", fontWeight = FontWeight.Medium)
                            if (hotel.amenities.isNotEmpty()) {
                                hotel.amenities.take(3).forEach { amenity ->
                                    Text("• $amenity")
                                }
                                if (hotel.amenities.size > 3) {
                                    Text("• +${hotel.amenities.size - 3} more")
                                }
                            } else {
                                Text("• No amenities listed")
                            }
                        }
                    } else {
                        Text("No hotels found matching your criteria")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// More Options Screen (for additional endpoints)
@Composable
fun MoreOptionsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "More API Options",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.navigate("advice") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Travel Advice", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("destination") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Destination Info", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("weather") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Weather", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("attractions") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Attractions", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("exchange") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Exchange Rates", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("youtube") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("YouTube Search", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("chatbot") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Chatbot", color = Color.White)
        }
    }
}
// Travel Advice Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAdviceScreen(viewModel: TravelPlannerViewModel) {
    var destination by remember { mutableStateOf("Thailand") }
    var travelDates by remember { mutableStateOf(getCurrentDateString()) }
    var durationDays by remember { mutableStateOf("10") }
    var questionsText by remember { mutableStateOf("Is it safe for solo travelers?, What's the best time to visit?") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Travel Advice",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = travelDates,
            onValueChange = { travelDates = it },
            label = { Text("Travel Dates (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = durationDays,
            onValueChange = { durationDays = it },
            label = { Text("Duration (days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = questionsText,
            onValueChange = { questionsText = it },
            label = { Text("Specific Questions (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val questions = questionsText.split(",").map { it.trim() }
                viewModel.generateTravelAdvice(
                    destination = destination,
                    travelDates = travelDates.takeIf { it.isNotBlank() },
                    durationDays = durationDays.toIntOrNull(),
                    specificQuestions = questions
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Get Advice", color = Color.White)
        }

        // Show results
        viewModel.travelAdviceResult.value?.let { advice ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Weather Information",
                        fontWeight = FontWeight.Bold
                    )
                    Text(advice.weather_info)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Local Customs",
                        fontWeight = FontWeight.Bold
                    )
                    if (advice.local_customs.isNotEmpty()) {
                        advice.local_customs.forEach { custom ->
                            Text("• $custom")
                        }
                    } else {
                        Text("No local customs information available")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Safety Tips",
                        fontWeight = FontWeight.Bold
                    )
                    if (advice.safety_tips.isNotEmpty()) {
                        advice.safety_tips.forEach { tip ->
                            Text("• $tip")
                        }
                    } else {
                        Text("No safety tips available")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Packing Suggestions",
                        fontWeight = FontWeight.Bold
                    )
                    if (advice.packing_suggestions.isNotEmpty()) {
                        advice.packing_suggestions.forEach { suggestion ->
                            Text("• $suggestion")
                        }
                    } else {
                        Text("No packing suggestions available")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Visa Requirements",
                        fontWeight = FontWeight.Bold
                    )
                    Text(advice.visa_requirements)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Currency Info",
                        fontWeight = FontWeight.Bold
                    )
                    Text(advice.currency_info)

                    if (advice.answers_to_questions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Answers to Your Questions",
                            fontWeight = FontWeight.Bold
                        )

                        advice.answers_to_questions.forEach { (question, answer) ->
                            Text(question, fontWeight = FontWeight.Medium)
                            Text(answer)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }


        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// Destination Screen - Fixed to handle structured response data properly
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationScreen(viewModel: TravelPlannerViewModel) {
    var location by remember { mutableStateOf("Barcelona") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Destination Information",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.getDestinationInfo(location)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Get Information", color = Color.White)
        }

        // Show results
        viewModel.destinationInfoResult.value?.let { info ->
            // Inspect the actual response structure
            Log.d("DestinationScreen", "Got destination response: $info")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Handle the nested destination_info structure
                    val destinationName = if (info.name.isNotEmpty()) info.name else "Unknown location"

                    Text(
                        text = destinationName,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (info.description.isNotEmpty()) {
                        Text(info.description)
                    } else {
                        Text("No description available")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Practical Information",
                        fontWeight = FontWeight.Bold
                    )
                    Text("Language: ${info.language.takeIf { it.isNotEmpty() } ?: "Not specified"}")
                    Text("Currency: ${info.currency.takeIf { it.isNotEmpty() } ?: "Not specified"}")
                    Text("Time Zone: ${info.time_zone.takeIf { it.isNotEmpty() } ?: "Not specified"}")
                    Text("Weather: ${info.weather_summary.takeIf { it.isNotEmpty() } ?: "Not specified"}")
                    Text("Safety Index: ${info.safety_index.takeIf { it.isNotEmpty() } ?: "Not specified"}")
                    Text("Cost Level: ${info.cost_level.takeIf { it.isNotEmpty() } ?: "Not specified"}")

                    // Only show highlights if available
                    if (info.highlights.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Highlights",
                            fontWeight = FontWeight.Bold
                        )
                        info.highlights.forEach { highlight ->
                            Text("• $highlight")
                        }
                    }

                    // Only show best time to visit if available
                    if (info.best_time_to_visit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Best Time to Visit",
                            fontWeight = FontWeight.Bold
                        )
                        Text(info.best_time_to_visit)
                    }

                    // Only show local phrases if available
                    if (info.local_phrases.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Local Phrases",
                            fontWeight = FontWeight.Bold
                        )
                        info.local_phrases.entries.take(5).forEach { (phrase, translation) ->
                            Text("$phrase: $translation")
                        }
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }
    }
}
// Weather Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: TravelPlannerViewModel) {
    var location by remember { mutableStateOf("Tokyo") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Weather Information",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.getWeather(location)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Get Weather", color = Color.White)
        }

        // Show results
        viewModel.weatherResult.value?.let { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Current Weather in ${weather.location}",
                        fontWeight = FontWeight.Bold
                    )

                    Text("Condition: ${weather.condition}")
                    Text("Temperature: ${weather.temperature.celsius}°C / ${weather.temperature.fahrenheit}°F")
                    Text("Humidity: ${weather.humidity}%")

                    // Safe access to Wind properties
                    val windSpeed = weather.wind.speed ?: 0.0
                    val windUnit = weather.wind.unit ?: "km/h"
                    val windDirection = weather.wind.direction ?: "N/A"
                    Text("Wind: $windSpeed $windUnit $windDirection")

                    Text("Updated at: ${weather.updated_at}")

                    // Only show forecast if available
                    if (weather.forecast.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Forecast",
                            fontWeight = FontWeight.Bold
                        )

                        weather.forecast.forEach { forecastTime ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("${forecastTime.time}: ${forecastTime.condition}")
                                    Text("Temperature: ${forecastTime.temperature.celsius}°C / ${forecastTime.temperature.fahrenheit}°F")
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No forecast data available")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }
    }
}
// Attractions Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionsScreen(viewModel: TravelPlannerViewModel) {
    var location by remember { mutableStateOf("Rome") }
    var radius by remember { mutableStateOf("10000") }
    var limit by remember { mutableStateOf("20") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Nearby Attractions",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = radius,
            onValueChange = { radius = it },
            label = { Text("Radius (meters, 1000-50000)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = limit,
            onValueChange = { limit = it },
            label = { Text("Results Limit (1-50)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.getAttractions(
                    location = location,
                    radius = radius.toIntOrNull() ?: 10000,
                    limit = limit.toIntOrNull() ?: 20
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Find Attractions", color = Color.White)
        }

        // Show results
        viewModel.attractionsResult.value?.let { attractionsResponse ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Attractions near $location",
                        fontWeight = FontWeight.Bold
                    )

                    val features = attractionsResponse.features
                    Text("Found ${features.size} attractions")

                    Spacer(modifier = Modifier.height(12.dp))

                    if (features.isNotEmpty()) {
                        features.forEach { feature ->
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            val displayName = if (feature.name.isNotEmpty()) feature.name else "Unnamed Attraction"
                            Text(displayName, fontWeight = FontWeight.Bold)

                            val kindsList = feature.kinds.split(",")
                            if (kindsList.isNotEmpty()) {
                                Text("Type: ${kindsList.first().capitalize()}")
                            }

                            Text("Rating: ${feature.rate}/10")
                            Text("Distance: ${feature.dist.toInt()}m")

                            feature.wikidata?.let {
                                Text(
                                    "Wikidata: $it",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Text("No attractions found in this area")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// Helper extension function
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}
// Exchange Rates Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRatesScreen(viewModel: TravelPlannerViewModel) {
    var baseCurrency by remember { mutableStateOf("USD") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Exchange Rates",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Base currency dropdown
        var expanded by remember { mutableStateOf(false) }
        val currencyOptions = listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR")

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = baseCurrency,
                onValueChange = { },
                label = { Text("Base Currency") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, "dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                currencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            baseCurrency = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.getExchangeRates(baseCurrency)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Get Exchange Rates", color = Color.White)
        }

        // Show results
        viewModel.exchangeRatesResult.value?.let { rates ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Use base_code from API response
                    Text(
                        "Exchange Rates (Base: ${rates.base_code})",
                        fontWeight = FontWeight.Bold
                    )

                    // Use time_last_update_utc from API response
                    Text("Updated at: ${rates.time_last_update_utc}")
                    Text("Next update: ${rates.time_next_update_utc}")

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display rates in a sorted manner using conversion_rates from API
                    if (rates.conversion_rates.isNotEmpty()) {
                        rates.conversion_rates.entries.sortedBy { it.key }.forEach { (currency, rate) ->
                            Text("$currency: $rate")
                        }
                    } else {
                        Text("No exchange rates available")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// YouTube Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeScreen(viewModel: TravelPlannerViewModel) {
    var query by remember { mutableStateOf("travel tips paris") }
    var maxResults by remember { mutableStateOf("10") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "YouTube Search",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Query") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxResults,
            onValueChange = { maxResults = it },
            label = { Text("Max Results (1-10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.searchYouTube(
                    query = query,
                    maxResults = maxResults.toIntOrNull() ?: 10
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Search Videos", color = Color.White)
        }

        // Show results
        viewModel.youtubeSearchResult.value?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "YouTube Search Results",
                        fontWeight = FontWeight.Bold
                    )

                    Text("Found ${result.videos.size} videos")

                    Spacer(modifier = Modifier.height(12.dp))

                    if (result.videos.isNotEmpty()) {
                        result.videos.forEach { video ->
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(video.title, fontWeight = FontWeight.Bold)
                            Text("Channel: ${video.channel_title}")
                            Text("Published: ${formatPublishDate(video.publish_time)}")

                            if (video.description.isNotEmpty()) {
                                Text(video.description)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    // Open YouTube app or browser with the video
                                    openYouTubeVideo(context, video.video_id)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF0000) // YouTube red color
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Watch on YouTube", color = Color.White)
                            }
                        }
                    } else {
                        Text("No videos found for your query")
                    }
                }
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
// Helper function to open a YouTube video in the YouTube app or browser
fun openYouTubeVideo(context: android.content.Context, videoId: String) {
    try {
        // Try to open in the YouTube app
        val youtubeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        if (youtubeAppIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(youtubeAppIntent)
        } else {
            // If YouTube app is not available, open in browser
            val youtubeBrowserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            context.startActivity(youtubeBrowserIntent)
        }
    } catch (e: Exception) {
        // If something goes wrong, fallback to browser
        val fallbackIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        context.startActivity(fallbackIntent)
        Log.e("YouTubeScreen", "Error opening YouTube app: ${e.message}", e)
    }
}
// Helper function to format the publish date
fun formatPublishDate(dateString: String): String {
    return try {
        // Input format: 2025-05-15T08:01:01Z
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)

        // Output format: May 15, 2025
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        outputFormat.format(date ?: return dateString)
    } catch (e: Exception) {
        dateString // Return original string if parsing fails
    }
}// Chatbot Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(viewModel: TravelPlannerViewModel) {
    var message by remember { mutableStateOf("What are the best beaches in Thailand?") }
    var useContext by remember { mutableStateOf(false) }
    var chatHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Travel Chatbot",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Chat history
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            items(chatHistory) { (userMessage, botResponse) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("You: $userMessage", fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Chatbot: $botResponse")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = useContext,
                onCheckedChange = { useContext = it }
            )
            Text("Use Context")
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Your Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (message.isNotBlank()) {
                    val currentMessage = message
                    if (useContext) {
                        viewModel.chatWithContext(currentMessage)
                    } else {
                        viewModel.chat(currentMessage)
                    }
                    message = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Send Message", color = Color.White)
        }

        // Add new message to chat history
        LaunchedEffect(viewModel.chatbotResult.value) {
            viewModel.chatbotResult.value?.let { result ->
                chatHistory = chatHistory + (message to result.response)
            }
        }

        // Show error if any
        viewModel.errorMessage.value?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }
    }
}
// =================== VIEW MODEL ===================

class TravelPlannerViewModel : ViewModel() {
    private val repository = TravelRepository()
    private val tag = "TravelPlannerViewModel"

    // Mutable states for API responses
    private val _budgetResult = mutableStateOf<BudgetResponse?>(null)
    val budgetResult: State<BudgetResponse?> = _budgetResult

    private val _tripPlanResult = mutableStateOf<TripPlanResponse?>(null)
    val tripPlanResult: State<TripPlanResponse?> = _tripPlanResult

    private val _travelAdviceResult = mutableStateOf<TravelAdviceResponse?>(null)
    val travelAdviceResult: State<TravelAdviceResponse?> = _travelAdviceResult

    private val _hotelSearchResult = mutableStateOf<HotelSearchResponse?>(null)
    val hotelSearchResult: State<HotelSearchResponse?> = _hotelSearchResult

    private val _destinationInfoResult = mutableStateOf<DestinationResponse?>(null)
    val destinationInfoResult: State<DestinationResponse?> = _destinationInfoResult

    private val _weatherResult = mutableStateOf<WeatherResponse?>(null)
    val weatherResult: State<WeatherResponse?> = _weatherResult

    private val _attractionsResult = mutableStateOf<AttractionResponse?>(null)
    val attractionsResult: State<AttractionResponse?> = _attractionsResult

    private val _exchangeRatesResult = mutableStateOf<ExchangeRatesResponse?>(null)
    val exchangeRatesResult: State<ExchangeRatesResponse?> = _exchangeRatesResult

    private val _youtubeSearchResult = mutableStateOf<YouTubeSearchResponse?>(null)
    val youtubeSearchResult: State<YouTubeSearchResponse?> = _youtubeSearchResult

    private val _chatbotResult = mutableStateOf<ChatbotResponse?>(null)
    val chatbotResult: State<ChatbotResponse?> = _chatbotResult

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Budget Generation
    fun generateBudget(
        destination: String,
        durationDays: Int,
        travelers: Int,
        budgetLevel: String,
        includeFlights: Boolean = true,
        includeAccommodation: Boolean = true,
        includeActivities: Boolean = true,
        includeFood: Boolean = true,
        origin: String? = null,
        currency: String = "USD"
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Generating budget for $destination")
                repository.generateBudget(
                    destination = destination,
                    durationDays = durationDays,
                    travelers = travelers,
                    budgetLevel = budgetLevel,
                    includeFlights = includeFlights,
                    includeAccommodation = includeAccommodation,
                    includeActivities = includeActivities,
                    includeFood = includeFood,
                    origin = origin,
                    currency = currency
                ).fold(
                    onSuccess = {
                        _budgetResult.value = it
                        Log.d(tag, "Budget generated successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to generate budget: ${it.message}"
                        Log.e(tag, "Failed to generate budget", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error generating budget", e)
            }
        }
    }

    // Trip Planning
    fun generateTripPlan(
        destination: String,
        durationDays: Int,
        interests: List<String>,
        travelStyle: String,
        travelDates: String? = null,
        includeAccommodation: Boolean = true,
        includeTransportation: Boolean = true
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Generating trip plan for $destination")
                repository.generateTripPlan(
                    destination = destination,
                    durationDays = durationDays,
                    interests = interests,
                    travelStyle = travelStyle,
                    travelDates = travelDates,
                    includeAccommodation = includeAccommodation,
                    includeTransportation = includeTransportation
                ).fold(
                    onSuccess = {
                        _tripPlanResult.value = it
                        Log.d(tag, "Trip plan generated successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to generate trip plan: ${it.message}"
                        Log.e(tag, "Failed to generate trip plan", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error generating trip plan", e)
            }
        }
    }

    // Travel Advice
    fun generateTravelAdvice(
        destination: String,
        travelDates: String? = null,
        durationDays: Int? = null,
        specificQuestions: List<String>? = null
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Generating travel advice for $destination")
                repository.generateTravelAdvice(
                    destination = destination,
                    travelDates = travelDates,
                    durationDays = durationDays,
                    specificQuestions = specificQuestions
                ).fold(
                    onSuccess = {
                        _travelAdviceResult.value = it
                        Log.d(tag, "Travel advice generated successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to generate travel advice: ${it.message}"
                        Log.e(tag, "Failed to generate travel advice", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error generating travel advice", e)
            }
        }
    }

    // Hotel Search
    fun searchHotels(
        location: String,
        checkInDate: String,
        checkOutDate: String,
        guests: Int = 2
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Searching hotels in $location")
                repository.searchHotels(
                    location = location,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    guests = guests
                ).fold(
                    onSuccess = {
                        _hotelSearchResult.value = it
                        Log.d(tag, "Hotel search completed successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to search hotels: ${it.message}"
                        Log.e(tag, "Failed to search hotels", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error searching hotels", e)
            }
        }
    }

    // Destination Info
    fun getDestinationInfo(location: String) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Getting destination info for $location")
                repository.getDestinationInfo(location).fold(
                    onSuccess = {
                        _destinationInfoResult.value = it
                        Log.d(tag, "Destination info retrieved successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get destination info: ${it.message}"
                        Log.e(tag, "Failed to get destination info", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting destination info", e)
            }
        }
    }

    // Weather
    fun getWeather(location: String) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Getting weather for $location")
                repository.getWeather(location).fold(
                    onSuccess = {
                        _weatherResult.value = it
                        Log.d(tag, "Weather retrieved successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get weather: ${it.message}"
                        Log.e(tag, "Failed to get weather", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting weather", e)
            }
        }
    }

    // Attractions
    fun getAttractions(
        location: String,
        radius: Int = 10000,
        limit: Int = 20
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Getting attractions for $location")
                repository.getAttractions(
                    location = location,
                    radius = radius,
                    limit = limit
                ).fold(
                    onSuccess = {
                        _attractionsResult.value = it
                        Log.d(tag, "Attractions retrieved successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get attractions: ${it.message}"
                        Log.e(tag, "Failed to get attractions", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting attractions", e)
            }
        }
    }

    // Exchange Rates
    fun getExchangeRates(baseCurrency: String = "USD") {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Getting exchange rates for $baseCurrency")
                repository.getExchangeRates(baseCurrency).fold(
                    onSuccess = {
                        _exchangeRatesResult.value = it
                        Log.d(tag, "Exchange rates retrieved successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get exchange rates: ${it.message}"
                        Log.e(tag, "Failed to get exchange rates", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting exchange rates", e)
            }
        }
    }

    // YouTube Search
    fun searchYouTube(
        query: String,
        maxResults: Int = 10
    ) {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Searching YouTube videos for $query")
                repository.searchYouTube(
                    query = query,
                    maxResults = maxResults
                ).fold(
                    onSuccess = {
                        _youtubeSearchResult.value = it
                        Log.d(tag, "YouTube videos retrieved successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to search YouTube videos: ${it.message}"
                        Log.e(tag, "Failed to search YouTube videos", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error searching YouTube videos", e)
            }
        }
    }

    // Chatbot
    fun chat(message: String) {
        _errorMessage.value = null
        _chatbotResult.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Sending message to chatbot")
                repository.chat(message).fold(
                    onSuccess = {
                        _chatbotResult.value = it
                        Log.d(tag, "Chatbot response received successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get chatbot response: ${it.message}"
                        Log.e(tag, "Failed to get chatbot response", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting chatbot response", e)
            }
        }
    }

    fun chatWithContext(message: String) {
        _errorMessage.value = null
        _chatbotResult.value = null

        viewModelScope.launch {
            try {
                Log.d(tag, "Sending contextual message to chatbot")
                repository.chatWithContext(message).fold(
                    onSuccess = {
                        _chatbotResult.value = it
                        Log.d(tag, "Contextual chatbot response received successfully")
                    },
                    onFailure = {
                        _errorMessage.value = "Failed to get contextual chatbot response: ${it.message}"
                        Log.e(tag, "Failed to get contextual chatbot response", it)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error getting contextual chatbot response", e)
            }
        }
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}

// =================== UTILITY FUNCTIONS ===================

fun getCurrentDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}

fun getDateAfterDays(days: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, days)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
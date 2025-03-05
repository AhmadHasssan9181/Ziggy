package com.noobdev.Zibby

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.geojson.LineString
import java.util.Locale
import com.noobdev.Zibby.ORSClient

class HomeViewModel(
    private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val orsClient = ORSClient
    private val geocoder = Geocoder(context, Locale.getDefault())


    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _currentAddress = MutableStateFlow("")
    val currentAddress = _currentAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _routeLineString = MutableStateFlow<LineString?>(null)
    val routeLineString = _routeLineString.asStateFlow()

    fun updateErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun updateCurrentLocation(location: Pair<Double, Double>) {
        viewModelScope.launch {
            _currentLocation.value = location
            updateAddressFromLocation(location)
        }
    }

    private suspend fun updateAddressFromLocation(location: Pair<Double, Double>) {
        withContext(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val addresses = geocoder.getFromLocation(
                    location.first,
                    location.second,
                    1
                )

                val address = addresses?.firstOrNull()?.let { addr ->
                    buildString {
                        append(addr.thoroughfare ?: "")
                        if (!addr.thoroughfare.isNullOrEmpty() && !addr.locality.isNullOrEmpty()) {
                            append(", ")
                        }
                        append(addr.locality ?: "")
                        if (!addr.locality.isNullOrEmpty() && !addr.countryName.isNullOrEmpty()) {
                            append(", ")
                        }
                        append(addr.countryName ?: "")
                    }
                } ?: "${String.format("%.6f", location.first)}, ${String.format("%.6f", location.second)}"

                _currentAddress.value = address
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get address: ${e.message}"
                _currentAddress.value = "${String.format("%.6f", location.first)}, ${String.format("%.6f", location.second)}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchRoute(start: Pair<Double, Double>, end: Pair<Double, Double>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                Log.d("fetchRoute", "Requesting route from $start to $end")

                orsClient.getDirections(start, end) { result ->
                    viewModelScope.launch {
                        if (result != null) {
                            Log.d("fetchRoute", "Route fetched successfully: $result")
                            _routeLineString.value = result
                        } else {
                            Log.e("fetchRoute", "Failed to fetch route - result is null")
                            _errorMessage.value = "Failed to fetch route"
                        }
                        _isLoading.value = false
                    }
                }

            } catch (e: Exception) {
                Log.e("fetchRoute", "Error fetching route: ${e.message}")
                _errorMessage.value = "Error fetching route: ${e.message}"
                _isLoading.value = false
            }
        }
    }



    companion object {
        fun provideFactory(context: Context, savedStateHandle: SavedStateHandle): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        return HomeViewModel(context, savedStateHandle) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            LocalContext.current,
            SavedStateHandle()
        )
    )
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val routeLineString by viewModel.routeLineString.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Zibby",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("profile")
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("settings")
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("about")
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Your location",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentAddress,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                currentLocation?.let { location ->
                                    navController.navigate("navigation/${location.first},${location.second}")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = "Navigation")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LocationAwareMap(
                        viewModel = viewModel,
                        onLocationUpdate = { location ->
                            viewModel.updateCurrentLocation(location)
                        }
                    )
                    SearchBox(
                        onSearch = { searchQuery ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Searching for: $searchQuery",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    ServiceOptions(
                        onRideClick = {
                            currentLocation?.let { location ->
                                navController.navigate("ride/${location.first},${location.second}")
                            }
                        },
                        onRentClick = {
                            currentLocation?.let { location ->
                                navController.navigate("rent/${location.first},${location.second}")
                            }
                        },
                        onDeliveryClick = {
                            currentLocation?.let { location ->
                                navController.navigate("delivery/${location.first},${location.second}")
                            }
                        }
                    )
                    RecentLocations { coordinates ->
                        currentLocation?.let { current ->
                            viewModel.fetchRoute(current, coordinates)
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
@Composable
fun ServiceOptions(
    onRideClick: () -> Unit,
    onRentClick: () -> Unit,
    onDeliveryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ServiceButton(
            icon = Icons.Default.DirectionsCar,
            text = "Ride",
            onClick = onRideClick
        )
        ServiceButton(
            icon = Icons.Default.TwoWheeler,
            text = "Rent",
            onClick = onRentClick
        )
        ServiceButton(
            icon = Icons.Default.LocalShipping,
            text = "Delivery",
            onClick = onDeliveryClick
        )
    }
}

@Composable
fun ServiceButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp)
        )
        Text(text)
    }
}
@Composable
fun SearchBox(onSearch: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Where to?",
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ServiceOptions(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ServiceOption(
            title = "Where To",
            icon = Icons.Default.DirectionsCar,
            hasDiscount = true,
            onClick = { navController.navigate("whereTo") }
        )
        ServiceOption(
            title = "City to city",
            icon = Icons.Default.LocationCity,
            hasDiscount = false,
            onClick = { navController.navigate("cityToCity") }
        )
    }
}

@Composable
fun ServiceOption(
    title: String,
    icon: ImageVector,
    hasDiscount: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasDiscount) {
                Badge(
                    containerColor = Color(0xFFFF5722),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("%", color = Color.White)
                }
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun RecentLocations(onLocationSelected: (Pair<Double, Double>) -> Unit) {
    val locations = listOf(
        Triple("I-18 Street, 400", 73.0479, 33.6844),
        Triple("4 Street", 73.0551, 33.6892),
        Triple("METRO Islamabad", 73.0486, 33.6998),
        Triple("Dubai Plaza", 73.0595, 33.7104)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        locations.forEach { (name, longitude, latitude) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onLocationSelected(Pair(longitude, latitude)) },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(name, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
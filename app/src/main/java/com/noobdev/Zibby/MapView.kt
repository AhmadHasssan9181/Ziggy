package com.noobdev.Zibby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.OnCameraTrackingChangedListener
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Locale

// PART 1: GEOCODING API SERVICE
// Interface for the OpenCage geocoding service
interface GeocodingApiService {
    @GET("geocode/v1/json")
    fun searchLocation(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Call<Map<String, Any>>
}

// Geocoding client
object GeocodingClient {
    private const val BASE_URL = "https://api.opencagedata.com/"
    private const val API_KEY = "7197722b517046909c15f761c566b49c" // OpenCage API key

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GeocodingApiService by lazy {
        retrofit.create(GeocodingApiService::class.java)
    }

    fun searchLocation(
        query: String,
        onResult: (List<SearchResult>) -> Unit
    ) {
        api.searchLocation(query, API_KEY).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val results = mutableListOf<SearchResult>()
                    try {
                        val responseBody = response.body()
                        val resultsArray = responseBody?.get("results") as? List<*>

                        resultsArray?.forEach { resultItem ->
                            (resultItem as? Map<*, *>)?.let { item ->
                                val geometry = item["geometry"] as? Map<*, *>
                                val lat = geometry?.get("lat") as? Double
                                val lng = geometry?.get("lng") as? Double
                                val formatted = item["formatted"] as? String

                                if (lat != null && lng != null && formatted != null) {
                                    results.add(
                                        SearchResult(
                                            name = formatted,
                                            coordinates = Pair(lng, lat)
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("GeocodingClient", "Error parsing response: ${e.message}")
                    }
                    onResult(results)
                } else {
                    Log.e("GeocodingClient", "API Error: ${response.errorBody()?.string()}")
                    onResult(emptyList())
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("GeocodingClient", "Network Error: ${t.message}")
                onResult(emptyList())
            }
        })
    }
}

// Search result data class
data class SearchResult(
    val name: String,
    val coordinates: Pair<Double, Double> // longitude, latitude
)

// PART 2: ROUTING API SERVICE
// Interface for the OpenRouteService
interface ORSApiService {
    @Headers("Content-Type: application/json", "Authorization: 5b3ce3597851110001cf6248099f56e121c64067b5762a109e70ee9b")
    @POST("v2/directions/driving-car")
    fun getRoute(@Body body: RequestBody): Call<ORSResponse>
}

// Data models for the ORS API
data class ORSResponse(val routes: List<Route>)
data class Route(val geometry: String)
data class ORSRequestCoordinates(val coordinates: List<List<Double>>, val instructions: Boolean = true)

// Routing client
object ORSClient {
    private const val BASE_URL = "https://api.openrouteservice.org/"
    private const val API_KEY = "5b3ce3597851110001cf6248099f56e121c64067b5762a109e70ee9b" // ORS API key

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ORSApiService by lazy {
        retrofit.create(ORSApiService::class.java)
    }

    // Get directions between two points
    fun getDirections(
        start: Pair<Double, Double>, // (longitude, latitude)
        end: Pair<Double, Double>,
        onResult: (LineString?) -> Unit
    ) {
        // Create coordinates in the format the API expects: [[lng1, lat1], [lng2, lat2]]
        val coordinates = listOf(
            listOf(start.first, start.second),
            listOf(end.first, end.second)
        )

        // Create the request object and convert to JSON
        val requestCoordinates = ORSRequestCoordinates(coordinates)
        val jsonAdapter = Gson()
        val jsonBody = jsonAdapter.toJson(requestCoordinates)

        // Create the RequestBody for Retrofit
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        // Make the API call
        api.getRoute(requestBody).enqueue(object : Callback<ORSResponse> {
            override fun onResponse(call: Call<ORSResponse>, response: Response<ORSResponse>) {
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()?.geometry
                    val decodedRoute = route?.let { decodePolyline(it) }
                    onResult(decodedRoute)
                } else {
                    Log.e("ORS", "API Error: ${response.errorBody()?.string()}")

                    // Fallback to simple route if API fails
                    val lineString = createSimpleRoute(start, end)
                    onResult(lineString)
                }
            }

            override fun onFailure(call: Call<ORSResponse>, t: Throwable) {
                Log.e("ORS", "Network Error: ${t.message}")

                // Fallback to simple route if API fails
                val lineString = createSimpleRoute(start, end)
                onResult(lineString)
            }
        })
    }

    // Decode polyline and convert to MapLibre LineString
    private fun decodePolyline(encoded: String): LineString {
        val decoded = PolyUtil.decode(encoded)
            .map { Point.fromLngLat(it.longitude, it.latitude) }
        return LineString.fromLngLats(decoded)
    }

    // Create a simple route between two points (fallback)
    private fun createSimpleRoute(start: Pair<Double, Double>, end: Pair<Double, Double>): LineString {
        val midLon = (start.first + end.first) / 2 + 0.01
        val midLat = (start.second + end.second) / 2 + 0.01

        val points = mutableListOf(
            Point.fromLngLat(start.first, start.second),
            Point.fromLngLat(midLon, midLat),
            Point.fromLngLat(end.first, end.second)
        )

        return LineString.fromLngLats(points)
    }
}

// PART 3: VIEW MODEL INTERFACE
// Common interface for view models that need map functionality
interface MapViewModel {
    val currentLocation: StateFlow<Pair<Double, Double>?>
    val routeLineString: StateFlow<LineString?>

    fun updateCurrentLocation(location: Pair<Double, Double>)
    fun updateErrorMessage(message: String)
}

// PART 4: TRAVEL VIEW MODEL IMPLEMENTATION
// Main view model for the travel explorer app
class TravelViewModel(
    private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), MapViewModel {
    private val geocoder = Geocoder(context, Locale.getDefault())

    // Implement interface properties
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    override val currentLocation = _currentLocation.asStateFlow()

    private val _routeLineString = MutableStateFlow<LineString?>(null)
    override val routeLineString = _routeLineString.asStateFlow()

    // App state
    private val _currentAddress = MutableStateFlow("")
    val currentAddress = _currentAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _selectedDestination = MutableStateFlow<TravelPlace?>(null)
    val selectedDestination = _selectedDestination.asStateFlow()

    private val _nearbyAttractions = MutableStateFlow<List<TravelPlace>>(emptyList())
    val nearbyAttractions = _nearbyAttractions.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    // Data class for travel places (attractions, destinations, etc.)
    data class TravelPlace(
        val name: String,
        val location: String,
        val description: String,
        val category: String,
        val rating: Float,
        val coordinates: Pair<Double, Double>,
        val imageUrl: String? = null,
        val distance: String? = null
    )

    // Initial popular destinations
    private val _popularDestinations = MutableStateFlow<List<TravelPlace>>(
        listOf(
            TravelPlace(
                "Paris", "France",
                "City of Lights and Love",
                "Cultural", 4.7f,
                Pair(2.3522, 48.8566)
            ),
            TravelPlace(
                "Rome", "Italy",
                "Eternal City with rich history",
                "Historic", 4.8f,
                Pair(12.4964, 41.9028)
            ),
            TravelPlace(
                "Tokyo", "Japan",
                "Blend of traditional and ultramodern",
                "Urban", 4.8f,
                Pair(139.6503, 35.6762)
            ),
            TravelPlace(
                "New York", "USA",
                "The city that never sleeps",
                "Urban", 4.6f,
                Pair(-74.0060, 40.7128)
            )
        )
    )
    val popularDestinations = _popularDestinations.asStateFlow()

    // Implement interface methods
    override fun updateErrorMessage(message: String) {
        _errorMessage.value = message
    }

    override fun updateCurrentLocation(location: Pair<Double, Double>) {
        viewModelScope.launch {
            _currentLocation.value = location
            updateAddressFromLocation(location)
            fetchNearbyAttractions(location)
        }
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchResults.value = emptyList()
        }
    }

    // Convert coordinates to address using geocoder
    private suspend fun updateAddressFromLocation(location: Pair<Double, Double>) {
        withContext(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val addresses = geocoder.getFromLocation(
                    location.second, // latitude
                    location.first,  // longitude
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
                } ?: "${String.format("%.6f", location.second)}, ${String.format("%.6f", location.first)}"

                _currentAddress.value = address
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get address: ${e.message}"
                _currentAddress.value = "${String.format("%.6f", location.second)}, ${String.format("%.6f", location.first)}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Search for locations using the OpenCage API
    fun searchDestination(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                GeocodingClient.searchLocation(query) { results ->
                    viewModelScope.launch {
                        _searchResults.value = results
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Convert search result to TravelPlace and set as destination
    fun selectSearchResult(result: SearchResult) {
        val place = TravelPlace(
            name = result.name,
            location = result.name,
            description = "Selected destination",
            category = "Location",
            rating = 0.0f,
            coordinates = result.coordinates
        )

        selectDestination(place)
    }

    fun selectDestination(place: TravelPlace) {
        _selectedDestination.value = place
        _searchResults.value = emptyList()
        _isSearchActive.value = false

        _currentLocation.value?.let { currentLocation ->
            fetchRoute(currentLocation, place.coordinates)
        }
    }

    // Fetch nearby attractions based on current location
    private fun fetchNearbyAttractions(location: Pair<Double, Double>) {
        viewModelScope.launch {
            // Simulate API call
            delay(1000)

            val attractions = listOf(
                TravelPlace(
                    "Local Museum", "5 min away",
                    "Fascinating exhibits of local history and culture",
                    "Museum", 4.3f,
                    Pair(location.first + 0.01, location.second + 0.005),
                    distance = "0.8 km"
                ),
                TravelPlace(
                    "Central Park", "10 min away",
                    "Beautiful green space in the city center",
                    "Park", 4.5f,
                    Pair(location.first - 0.005, location.second + 0.01),
                    distance = "1.2 km"
                ),
                TravelPlace(
                    "Fine Dining", "15 min away",
                    "Award-winning local cuisine",
                    "Restaurant", 4.7f,
                    Pair(location.first + 0.015, location.second - 0.008),
                    distance = "1.5 km"
                )
            )

            _nearbyAttractions.value = attractions
        }
    }

    // Get route between two points using OpenRouteService
    fun fetchRoute(start: Pair<Double, Double>, end: Pair<Double, Double>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                ORSClient.getDirections(start, end) { result ->
                    viewModelScope.launch {
                        if (result != null) {
                            _routeLineString.value = result
                        } else {
                            _errorMessage.value = "Failed to fetch route"
                        }
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching route: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearRoute() {
        _routeLineString.value = null
        _selectedDestination.value = null
    }

    companion object {
        fun provideFactory(context: Context, savedStateHandle: SavedStateHandle): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
                        return TravelViewModel(context, savedStateHandle) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}

// PART 5: LOCATION-AWARE MAP COMPOSABLE
// Composable for displaying the map and handling location updates
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationAwareMap(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onLocationUpdate: (Pair<Double, Double>) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val currentLocation by viewModel.currentLocation.collectAsState()
    val routeLineString by viewModel.routeLineString.collectAsState()

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionsState = rememberMultiplePermissionsState(locationPermissions)
    val hasLocationPermission = permissionsState.allPermissionsGranted

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val handleLocationUpdate: (Pair<Double, Double>) -> Unit = { location ->
        onLocationUpdate(location)
        viewModel.updateCurrentLocation(location)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView?.onCreate(null)
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView?.onDestroy()
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                mapView = this
                getMapAsync { map ->
                    val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=U7fg3KGqTysSBCZJpaNH"
                    map.setStyle(styleUrl) { style ->
                        try {
                            if (hasLocationPermission &&
                                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val locationComponentOptions = LocationComponentOptions.builder(context)
                                    .pulseEnabled(true)
                                    .build()

                                map.locationComponent.apply {
                                    activateLocationComponent(
                                        LocationComponentActivationOptions.builder(context, style)
                                            .locationComponentOptions(locationComponentOptions)
                                            .build()
                                    )

                                    isLocationComponentEnabled = true
                                    renderMode = RenderMode.COMPASS
                                    cameraMode = CameraMode.TRACKING

                                    lastKnownLocation?.let { location ->
                                        handleLocationUpdate(Pair(location.longitude, location.latitude))
                                        map.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(location.latitude, location.longitude),
                                                15.0
                                            )
                                        )
                                    }

                                    addOnLocationClickListener {
                                        lastKnownLocation?.let { location ->
                                            handleLocationUpdate(Pair(location.longitude, location.latitude))
                                        }
                                    }

                                    addOnCameraTrackingChangedListener(object : OnCameraTrackingChangedListener {
                                        override fun onCameraTrackingDismissed() {
                                            lastKnownLocation?.let { location ->
                                                handleLocationUpdate(Pair(location.longitude, location.latitude))
                                            }
                                        }

                                        override fun onCameraTrackingChanged(currentMode: Int) {
                                            lastKnownLocation?.let { location ->
                                                handleLocationUpdate(Pair(location.longitude, location.latitude))
                                            }
                                        }
                                    })
                                }
                            }

                            // Set up route display
                            style.addSource(GeoJsonSource("route-source"))
                            style.addLayer(
                                LineLayer("route-layer", "route-source").apply {
                                    setProperties(
                                        PropertyFactory.lineColor("#4B89F0"),
                                        PropertyFactory.lineWidth(5f),
                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND)
                                    )
                                }
                            )
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                            viewModel.updateErrorMessage("Location permission error: ${e.message}")
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        update = { mapView ->
            routeLineString?.let { lineString ->
                mapView.getMapAsync { map ->
                    map.getStyle { style ->
                        val source = style.getSource("route-source") as? GeoJsonSource
                        source?.setGeoJson(lineString)

                        val coordinates = lineString.coordinates()
                        if (coordinates.isNotEmpty()) {
                            val boundsBuilder = LatLngBounds.Builder()
                            coordinates.forEach { point ->
                                boundsBuilder.include(LatLng(point.latitude(), point.longitude()))
                            }

                            map.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    boundsBuilder.build(),
                                    50
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

// PART 6: TRAVEL MAP SCREEN
// Main screen composable for the travel app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelMapScreen(
    navController: NavController,
    viewModel: TravelViewModel = viewModel(
        factory = TravelViewModel.provideFactory(
            LocalContext.current,
            SavedStateHandle()
        )
    )
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val focusManager = LocalFocusManager.current

    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val routeLineString by viewModel.routeLineString.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedDestination by viewModel.selectedDestination.collectAsState()
    val nearbyAttractions by viewModel.nearbyAttractions.collectAsState()
    val popularDestinations by viewModel.popularDestinations.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

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
                    text = "Travel Explorer",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    label = { Text("My Trips") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // navController.navigate("myTrips")
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Hotel, contentDescription = null) },
                    label = { Text("Accommodation") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // navController.navigate("hotels")
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // navController.navigate("profile")
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
                        if (isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search for a location...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.searchDestination(searchQuery)
                                        focusManager.clearFocus()
                                    }
                                )
                            )
                        } else {
                            Text(
                                text = "Travel Explorer",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    navigationIcon = {
                        if (isSearchActive) {
                            IconButton(onClick = {
                                viewModel.setSearchActive(false)
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    },
                    actions = {
                        if (isSearchActive) {
                            IconButton(onClick = {
                                viewModel.searchDestination(searchQuery)
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        } else {
                            IconButton(onClick = { viewModel.setSearchActive(true) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = {
                                // Recenter map on current location
                            }) {
                                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                            }
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    )

                    // Search results
                    AnimatedVisibility(
                        visible = searchResults.isNotEmpty(),
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {
                            items(searchResults) { result ->
                                ListItem(
                                    headlineContent = { Text(result.name) },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        viewModel.selectSearchResult(result)
                                    }
                                )
                                Divider()
                            }
                        }
                    }

                    // Main content: Selected destination or default screen
                    if (selectedDestination != null) {
                        // Show route information
                        RouteInformation(
                            destination = selectedDestination,
                            onClearRoute = { viewModel.clearRoute() },
                            onStartNavigation = {
                                // Handle navigation start
                            }
                        )
                    } else if (!isSearchActive && searchResults.isEmpty()) {
                        // Show default content
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            item {
                                // Current location card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Your Location",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            currentAddress,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Quick actions
                                QuickActions()

                                // Popular destinations
                                Text(
                                    "Popular Destinations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            items(popularDestinations) { destination ->
                                DestinationItem(
                                    destination = destination,
                                    onClick = { viewModel.selectDestination(destination) }
                                )
                            }

                            item {
                                // Nearby attractions
                                if (nearbyAttractions.isNotEmpty()) {
                                    Text(
                                        "Nearby Attractions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                    )
                                }
                            }

                            items(nearbyAttractions) { attraction ->
                                AttractionItem(
                                    attraction = attraction,
                                    onClick = { viewModel.selectDestination(attraction) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

// PART 7: UI COMPONENTS
// Search box composable
@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search for a location...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Navigation, contentDescription = "Search")
            }
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        )
    )
}

// Quick actions composable
@Composable
fun QuickActions() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Explore Nearby",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.Hotel,
                    label = "Hotels"
                )
                QuickActionItem(
                    icon = Icons.Default.Restaurant,
                    label = "Food"
                )
                QuickActionItem(
                    icon = Icons.Default.Attractions,
                    label = "Sights"
                )
                QuickActionItem(
                    icon = Icons.Default.DirectionsWalk,
                    label = "Tours"
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

// Destination item composable
@Composable
fun DestinationItem(
    destination: TravelViewModel.TravelPlace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for image
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = destination.name.first().toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = destination.location,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = destination.rating.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Attraction item composable
@Composable
fun AttractionItem(
    attraction: TravelViewModel.TravelPlace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val icon = when (attraction.category.lowercase()) {
                        "museum" -> Icons.Default.Museum
                        "park" -> Icons.Default.Park
                        "restaurant" -> Icons.Default.Restaurant
                        else -> Icons.Default.Place
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${attraction.distance} • ${attraction.category}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = attraction.rating.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Route information composable
@Composable
fun RouteInformation(
    destination: TravelViewModel.TravelPlace?,
    onClearRoute: () -> Unit,
    onStartNavigation: () -> Unit
) {
    destination?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Trip to ${destination.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            destination.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = onClearRoute) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(destination.description)

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RouteInfoItem(
                        icon = Icons.Default.DirectionsWalk,
                        label = "Distance",
                        value = "12.5 km" // Mock data
                    )
                    RouteInfoItem(
                        icon = Icons.Default.Timer,
                        label = "Duration",
                        value = "35 min" // Mock data
                    )
                    Button(
                        onClick = onStartNavigation,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Navigate")
                    }
                }
            }
        }
    }
}

// Route info item composable
@Composable
fun RouteInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
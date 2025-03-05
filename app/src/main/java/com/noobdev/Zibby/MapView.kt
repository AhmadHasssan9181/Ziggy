package com.noobdev.Zibby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.OnCameraTrackingChangedListener
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationAwareMap(
    viewModel: HomeViewModel,
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
            .height(300.dp),
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
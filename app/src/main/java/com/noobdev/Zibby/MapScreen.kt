package com.noobdev.Zibby

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.noobdev.Zibby.Dataclasses.ORSRequestBody
import com.noobdev.Zibby.Dataclasses.RetrofitInstance
import kotlinx.coroutines.*
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import java.nio.file.WatchEvent

// Theme colors
val lightGray = Color(0xFFF3F3F3)
val darkGray = Color(0xFF444444)
val orangeColor = Color(0xFFFF7700)
val blueColor = Color(0xFF2196F3)
val greenColor = Color(0xFF4CAF50)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onStart()
        onDispose { mapView.onStop() }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
    ) { view ->
        view.getMapAsync { mapLibreMap ->
            mapLibreMap.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) {
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(49.41461, 8.681495))
                    .zoom(14.0)
                    .build()
                mapLibreMap.cameraPosition = cameraPosition

                CoroutineScope(Dispatchers.IO).launch {
                    val start = listOf(8.681495, 49.41461)
                    val end = listOf(8.687872, 49.420318)
                    val body = ORSRequestBody(listOf(start, end))
                    val response = RetrofitInstance.api.getRoute(body, "YOUR_API_KEY_HERE")

                    if (response.isSuccessful) {
                        val coords = response.body()?.features?.firstOrNull()
                            ?.geometry?.coordinates ?: return@launch

                        val linePoints = coords.map {
                            val lng = (it as List<*>)[0] as Double
                            val lat = it[1] as Double
                            Point.fromLngLat(lng, lat)
                        }

                        val lineString = LineString.fromLngLats(linePoints)
                        val routeFeature = Feature.fromGeometry(lineString)

                        withContext(Dispatchers.Main) {
                            val geoJsonSource = GeoJsonSource("route-source", routeFeature)
                            val lineLayer = LineLayer("route-layer", "route-source")
                                .withProperties(
                                    lineColor(orangeColor.toString()), // Use our theme orange color
                                    lineWidth(5f)
                                )

                            mapLibreMap.style?.apply {
                                addSource(geoJsonSource)
                                addLayer(lineLayer)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun mapView(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray),
    ){
        Column(
            modifier = Modifier
                .height(600.dp) // Made map taller
                .fillMaxWidth()
                .padding(16.dp)
        ){
            MapScreen()
        }
        to_where_card(Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun to_where_card(modifier: Modifier){
    Card(
        modifier = modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .height(120.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // White card background
        ),
        shape = RoundedCornerShape(8.dp), // Rounded corners like BudgetScreen
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom=8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(30.dp),
                    tint = blueColor // Blue icon matching the theme
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Where to?",
                    fontSize = 18.sp,
                    color = darkGray, // Dark gray text
                    modifier = Modifier.padding(4.dp))
            }
            Divider(
                color = lightGray, // Light gray divider
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = "From",
                    modifier = Modifier.size(30.dp),
                    tint = greenColor // Green icon matching the theme
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("From Where?",
                    fontSize = 18.sp,
                    color = darkGray, // Dark gray text
                    modifier = Modifier.padding(4.dp))
            }
        }
    }
}
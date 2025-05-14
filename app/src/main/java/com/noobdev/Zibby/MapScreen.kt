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
        modifier = Modifier.fillMaxSize()
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
                                    org.maplibre.android.style.layers.PropertyFactory.lineColor("#FF0000"), // Use hex color for red
                                    org.maplibre.android.style.layers.PropertyFactory.lineWidth(5f)
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
           .fillMaxSize(),
   ){
       Column(
           modifier = Modifier
               .height(500.dp)
               .width(350.dp)
       ){
           MapScreen()
       }
       to_where_card(Modifier.align(Alignment.TopCenter))
   }
}

@Composable
fun to_where_card(modifier: Modifier){
    Card(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = Color.White,
            containerColor = Color.Gray
        )
    ){
        Column(
            modifier = Modifier
                .padding(8.dp)
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
                    contentDescription = "null",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text("Where to?",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(4.dp))
            }
            Divider(
                color = Color.White,
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = "null",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text("From Where?",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(4.dp))

            }

        }
    }
}



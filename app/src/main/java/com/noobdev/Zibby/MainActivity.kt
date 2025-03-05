package com.noobdev.Zibby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.noobdev.Zibby.ui.theme.ZiggyTheme
import android.Manifest
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(
            this,
            "",
            WellKnownTileServer.MapLibre
        )
        enableEdgeToEdge()
        setContent {
            ZiggyTheme {
                val navController = rememberNavController()
                Navigation(navController)
                RequestPermissions()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions() {
    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.CAMERA
    )

    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        multiplePermissionsState.launchMultiplePermissionRequest()
    }

    when {
        multiplePermissionsState.allPermissionsGranted -> {
            Text("All permissions granted!")
        }
        multiplePermissionsState.shouldShowRationale -> {
            Column {
                Text("The app needs these permissions to work properly.")
                Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permissions")
                }
            }
        }
        else -> {
            Text("Permissions denied. Please enable them in settings.")
        }
    }
}